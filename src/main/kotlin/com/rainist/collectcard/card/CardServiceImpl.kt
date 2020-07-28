package com.rainist.collectcard.card

import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.ApiLog
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
import com.rainist.collectcard.common.db.entity.makeCardEntity
import com.rainist.collectcard.common.db.entity.makeCardHistoryEntity
import com.rainist.collectcard.common.db.repository.CardHistoryRepository
import com.rainist.collectcard.common.db.repository.CardRepository
import com.rainist.collectcard.common.dto.SyncRequest
import com.rainist.collectcard.common.exception.CollectcardException
import com.rainist.collectcard.common.service.ApiLogService
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.util.SyncStatus
import com.rainist.common.log.Log
import com.rainist.common.util.DateTimeUtil
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CardServiceImpl(
    val apiLogService: ApiLogService,
    val collectExecutorService: CollectExecutorService,
    val headerService: HeaderService,
    val cardRepository: CardRepository,
    val cardHistoryRepository: CardHistoryRepository
) : CardService {

    companion object : Log

    @Transactional
    @SyncStatus(transactionId = "cards")
    override fun listCards(syncRequest: SyncRequest): ListCardsResponse {
        logger.info("CardService.listCards start: syncRequest: {}", syncRequest)

        /* header */
        val header = headerService.makeHeader(syncRequest.banksaladUserId, syncRequest.organizationId)

        /* request body */
        val listCardsRequest = ListCardsRequest().apply {
            dataBody = ListCardsRequestDataBody()
        }

        /* Call API */
        val executionResponse: ExecutionResponse<ListCardsResponse> =
            collectExecutorService.execute(
                Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.cards),
                ExecutionRequest.builder<ListCardsRequest>()
                    .headers(header)
                    .request(listCardsRequest)
                    .build(),
                { apiLog: ApiLog ->
                    apiLogService.logRequest(syncRequest.organizationId, syncRequest.banksaladUserId.toLong(), apiLog)
                },
                { apiLog: ApiLog ->
                    apiLogService.logResponse(syncRequest.organizationId, syncRequest.banksaladUserId.toLong(), apiLog)
                }
            )

        /* response error handling */
        // TODO : error handling
        if (!HttpStatus.valueOf(executionResponse.httpStatusCode).is2xxSuccessful) {
            throw CollectcardException("Resopnse status is not success")
        }

        val listCardsResponse = executionResponse.response

        /* convert type and format if necessary */
        listCardsResponse.dataBody?.cards?.forEach { card ->
            card.cardNumber = card.cardNumber?.replace("-", "")
        }

        /* Save to DB and return */
        listCardsResponse.dataBody?.cards?.forEach { card ->
            upsertCardAndCardHistory(syncRequest.banksaladUserId, syncRequest.organizationId ?: "", card)
        }

        logger.info("CardService.listCards end: syncRequest: {}", syncRequest)

        return executionResponse.response
    }

    private fun upsertCardAndCardHistory(banksaladUserId: String, cardCompanyId: String, card: Card) {
        /* 카드 조회 */
        cardRepository.findByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardId(banksaladUserId.toLong(), cardCompanyId, card.cardCompanyCardId ?: "")
            ?.let { cardEntity ->
                /* 기존 카드 */

                val prevUpdatedAt = cardEntity.updatedAt
                val bodyEntity = cardEntity.makeCardEntity(banksaladUserId.toLong(), cardCompanyId, card)

                val saveEntity = cardRepository.saveAndFlush(bodyEntity)

                if (true == saveEntity.updatedAt?.isAfter(prevUpdatedAt)) {
                    /* insert card_history */
                    cardHistoryRepository.save(CardHistoryEntity().makeCardHistoryEntity(cardEntity))
                }

                saveEntity.lastCheckAt = DateTimeUtil.utcNowLocalDateTime()
                cardRepository.save(saveEntity)
            }
            ?: kotlin.run {
                /*  신규 카드 */

                /* insert new card */
                val cardEntity = CardEntity().makeCardEntity(banksaladUserId.toLong(), cardCompanyId, card)
                cardRepository.save(cardEntity)

                /* insert card_history */
                cardHistoryRepository.save(CardHistoryEntity().makeCardHistoryEntity(cardEntity))
            }
    }
}
