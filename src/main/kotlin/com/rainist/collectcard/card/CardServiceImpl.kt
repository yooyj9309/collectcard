package com.rainist.collectcard.card

import com.rainist.collect.common.execution.ExecutionContext
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
import com.rainist.collectcard.common.enums.ResultCode
import com.rainist.collectcard.common.exception.CollectcardException
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.service.UserSyncStatusService
import com.rainist.common.log.Log
import com.rainist.common.util.DateTimeUtil
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

    override fun listCards(executionContext: ExecutionContext): ListCardsResponse {
        logger.info("CardService.listCards start: executionContext: {}", executionContext)

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
            upsertCardAndCardHistory(executionContext.userId.toLong(), card)
        }

        /* check response result */
        validateResponseAndThrow(executionResponse)

        userSyncStatusService.updateUserSyncStatus(
            banksaladUserId,
            executionContext.organizationId,
            Transaction.cards.name,
            DateTimeUtil.utcLocalDateTimeToEpochMilliSecond(executionContext.startAt)
        )

        logger.info("CardService.listCards end: executionContext: {}", executionContext)
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
        val entityDto = cardMapper.toCardDto(cardEntity)

        if (entityDto.unequals(card)) {
            /* update field */
            cardMapper.merge(card, cardEntity)
            cardEntity.lastCheckAt = DateTimeUtil.utcNowLocalDateTime()

            val cardHistoryEntity = cardMapper.toCardHistoryEntity(cardEntity)
            cardHistoryRepository.save(cardHistoryEntity)
        }

        cardRepository.save(cardEntity)
    }

    /* 신규 카드 */
    private fun insertCardEntity(card: Card, banksaladUserId: Long) {
        val cardEntity = cardMapper.toCardEntity(card).apply {
            this.banksaladUserId = banksaladUserId
            this.lastCheckAt = DateTimeUtil.utcNowLocalDateTime()
        }
        cardRepository.save(cardEntity)

        val cardHistoryEntity =
            cardMapper.toCardHistoryEntity(cardEntity) // modelMapper.map(cardEntity, CardHistoryEntity::class.java)
        cardHistoryRepository.save(cardHistoryEntity)
    }

    private fun validateResponseAndThrow(executionResponse: ExecutionResponse<ListCardsResponse>) {
        /* check response result */
        if (executionResponse.isExceptionOccurred) {
            throw CollectcardException(ResultCode.UNKNOWN.name)
        }

        val listCardsResponse = executionResponse.response

        if (listCardsResponse.resultCodes.contains(ResultCode.EXTERNAL_SERVER_ERROR)) {
            throw CollectcardException(ResultCode.EXTERNAL_SERVER_ERROR.name, "")
        }

        if (listCardsResponse.resultCodes.contains(ResultCode.INVALID_ACCESS_TOKEN)) {
            throw CollectcardException(ResultCode.INVALID_ACCESS_TOKEN.name, "")
        }

        if (listCardsResponse.resultCodes.contains(ResultCode.INVALID_USER)) {
            throw CollectcardException(ResultCode.INVALID_USER.name, "")
        }

        if (listCardsResponse.resultCodes.contains(ResultCode.UNKNOWN)) {
            throw CollectcardException(ResultCode.UNKNOWN.name, "")
        }
    }
}
