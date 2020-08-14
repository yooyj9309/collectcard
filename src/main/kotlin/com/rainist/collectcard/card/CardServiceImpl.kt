package com.rainist.collectcard.card

import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.card.dto.Card
import com.rainist.collectcard.card.dto.ListCardsRequest
import com.rainist.collectcard.card.dto.ListCardsRequestDataBody
import com.rainist.collectcard.card.dto.ListCardsResponse
import com.rainist.collectcard.card.mapper.CardMapper
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.common.db.entity.CardEntity
import com.rainist.collectcard.common.db.repository.CardHistoryRepository
import com.rainist.collectcard.common.db.repository.CardRepository
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.service.UserSyncStatusService
import com.rainist.collectcard.common.util.ExecutionResponseValidator
import com.rainist.common.log.Log
import com.rainist.common.util.DateTimeUtil
import java.time.LocalDateTime
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service

@Service
class CardServiceImpl(
    val userSyncStatusService: UserSyncStatusService,
    val headerService: HeaderService,
    val collectExecutorService: CollectExecutorService,
    val cardRepository: CardRepository,
    val cardHistoryRepository: CardHistoryRepository
) : CardService {

    companion object : Log

    val cardMapper = Mappers.getMapper(CardMapper::class.java)

    override fun listCards(executionContext: CollectExecutionContext): ListCardsResponse {
        logger.info("CardService.listCards start: executionContext: {}", executionContext)

        val now = DateTimeUtil.utcNowLocalDateTime()
        val banksaladUserId = executionContext.userId.toLong()

        /* header */
        val header = headerService.makeHeader(executionContext.userId, executionContext.organizationId)

        /* request body */
        val listCardsRequest = ListCardsRequest().apply {
            dataBody = ListCardsRequestDataBody()
        }

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

        /* check response result */
        ExecutionResponseValidator.validateResponseAndThrow(
            executionResponse,
            executionResponse.response.resultCodes)

        val listCardsResponse = executionResponse.response

        /* convert type and format if necessary */
        listCardsResponse?.dataBody?.cards?.forEach { card ->
            card.apply {
                cardNumber = cardNumber?.replace("-", "")
                cardNumberMask = cardNumberMask?.replace("-", "")
                cardCompanyId = executionContext.organizationId
            }
        }

        /* Save to DB and return */
        listCardsResponse?.dataBody?.cards?.forEach { card ->
            upsertCardAndCardHistory(executionContext.userId.toLong(), card, now)
        }

        userSyncStatusService.updateUserSyncStatus(
            banksaladUserId,
            executionContext.organizationId,
            Transaction.cards.name,
            DateTimeUtil.utcLocalDateTimeToEpochMilliSecond(now)
        )

        logger.info("CardService.listCards end: executionContext: {}", executionContext)
        return executionResponse.response
    }

    private fun upsertCardAndCardHistory(banksaladUserId: Long, card: Card, now: LocalDateTime) {
        /* 카드 조회 */
        cardRepository.findByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardId(
            banksaladUserId,
            card.cardCompanyId ?: "",
            card.cardCompanyCardId ?: ""
        )
            ?.let { cardEntity ->
                updateCardEntity(cardEntity, card, now)
            }
            ?: kotlin.run {
                insertCardEntity(card, banksaladUserId, now)
            }
    }

    /* 기존 카드 */
    private fun updateCardEntity(cardEntity: CardEntity, card: Card, now: LocalDateTime) {
        val entityDto = cardMapper.toCardDto(cardEntity)

        if (entityDto.unequals(card)) {
            /* update field */
            cardMapper.merge(card, cardEntity)
            cardEntity.lastCheckAt = now

            cardRepository.save(cardEntity)

            val cardHistoryEntity = cardMapper.toCardHistoryEntity(cardEntity)
            cardHistoryRepository.save(cardHistoryEntity)
        }
    }

    /* 신규 카드 */
    private fun insertCardEntity(card: Card, banksaladUserId: Long, now: LocalDateTime) {
        val cardEntity = cardMapper.toCardEntity(card).apply {
            this.banksaladUserId = banksaladUserId
            this.lastCheckAt = now
        }
        cardRepository.save(cardEntity)

        val cardHistoryEntity =
            cardMapper.toCardHistoryEntity(cardEntity) // modelMapper.map(cardEntity, CardHistoryEntity::class.java)
        cardHistoryRepository.save(cardHistoryEntity)
    }
}
