package com.rainist.collectcard.card

import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.card.dto.Card
import com.rainist.collectcard.card.dto.ListCardsRequest
import com.rainist.collectcard.card.dto.ListCardsRequestDataBody
import com.rainist.collectcard.card.dto.ListCardsResponse
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.common.db.entity.CardEntity
import com.rainist.collectcard.common.db.entity.CardHistoryEntity
import com.rainist.collectcard.common.db.repository.CardHistoryRepository
import com.rainist.collectcard.common.db.repository.CardRepository
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.dto.SyncRequest
import com.rainist.collectcard.common.exception.CollectcardException
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.util.SyncStatus
import com.rainist.common.log.Log
import com.rainist.common.util.DateTimeUtil
import org.modelmapper.ModelMapper
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CardServiceImpl(
    val collectExecutorService: CollectExecutorService,
    val headerService: HeaderService,
    val cardRepository: CardRepository,
    val cardHistoryRepository: CardHistoryRepository,
    val modelMapper: ModelMapper
) : CardService {

    companion object : Log

    @Transactional
    @SyncStatus(transactionId = "cards")
    override fun listCards(syncRequest: SyncRequest): ListCardsResponse {
        logger.info("CardService.listCards start: syncRequest: {}", syncRequest)

        /* header */
        val header = headerService.makeHeader(syncRequest.banksaladUserId.toString(), syncRequest.organizationId)

        /* request body */
        val listCardsRequest = ListCardsRequest().apply {
            dataBody = ListCardsRequestDataBody()
        }

        /* Execution Context */
        val executionContext: ExecutionContext = CollectExecutionContext(
            organizationId = syncRequest.organizationId,
            userId = syncRequest.banksaladUserId.toString(),
            startAt = DateTimeUtil.utcNowLocalDateTime()
        )

        /* Call API */
        val executionResponse: ExecutionResponse<ListCardsResponse> =
            collectExecutorService.execute(
                executionContext,
                Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.cards),
                ExecutionRequest.builder<ListCardsRequest>()
                    .headers(header)
                    .request(listCardsRequest)
                    .build()
            )

        /* response error handling */
        // TODO : error handling
        if (!HttpStatus.valueOf(executionResponse.httpStatusCode).is2xxSuccessful) {
            throw CollectcardException("Resopnse status is not success")
        }

        val listCardsResponse = executionResponse.response

        /* convert type and format if necessary */
        listCardsResponse.dataBody?.cards?.forEach { card ->
            card.apply {
                cardNumber = cardNumber?.replace("-", "")
                cardNumberMask = cardNumberMask?.replace("-", "")
                cardCompanyId = syncRequest.organizationId
            }
        }

        /* Save to DB and return */
        listCardsResponse.dataBody?.cards?.forEach { card ->
            upsertCardAndCardHistory(syncRequest.banksaladUserId, card)
        }

        logger.info("CardService.listCards end: syncRequest: {}", syncRequest)

        return executionResponse.response
    }

    private fun upsertCardAndCardHistory(banksaladUserId: Long, card: Card) {
        /* 카드 조회 */
        cardRepository.findByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardId(
            banksaladUserId.toLong(),
            card.cardCompanyId ?: "",
            card.cardCompanyCardId ?: ""
        )
            ?.let { cardEntity ->
                updateCardEntity(cardEntity, card)
            }
            ?: kotlin.run {
                insertCardEntity(card, banksaladUserId)
            }
    }

    /* 기존 카드 */
    private fun updateCardEntity(cardEntity: CardEntity, card: Card) {
        val entityDto = modelMapper.map(cardEntity, Card::class.java)

        if (entityDto.unequals(card)) {
            /* update field */
            modelMapper.map(card, cardEntity)
            cardEntity.lastCheckAt = DateTimeUtil.utcNowLocalDateTime()

            val cardHistoryEntity = modelMapper.map(cardEntity, CardHistoryEntity::class.java)
            cardHistoryRepository.save(cardHistoryEntity)
        }

        cardRepository.save(cardEntity)
    }

    /* 신규 카드 */
    private fun insertCardEntity(card: Card, banksaladUserId: Long) {

        val cardEntity = modelMapper.map(card, CardEntity::class.java).apply {
            this.banksaladUserId = banksaladUserId
            this.lastCheckAt = DateTimeUtil.utcNowLocalDateTime()
        }
        cardRepository.save(cardEntity)

        val cardHistoryEntity = modelMapper.map(cardEntity, CardHistoryEntity::class.java)
        cardHistoryRepository.save(cardHistoryEntity)
    }
}
