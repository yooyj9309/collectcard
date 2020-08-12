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
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.dto.SyncRequest
import com.rainist.collectcard.common.enums.ResultCode
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.service.UserSyncStatusService
import com.rainist.common.log.Log
import com.rainist.common.util.DateTimeUtil
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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

    @Transactional
    override fun listCards(syncRequest: SyncRequest): ListCardsResponse {
        logger.info("CardService.listCards start: syncRequest: {}", syncRequest)

        val userSyncStatusLastCheckedAt = DateTimeUtil.utcNowLocalDateTime()

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

//        if (executionResponse.isExceptionOccurred){
//            TODO("excution exception handling")
//        }

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

        val isSuccess = listCardsResponse.resultCodes.filter { it != ResultCode.OK }.isEmpty()
        if (isSuccess) {
            userSyncStatusService.updateUserSyncStatus(syncRequest.banksaladUserId, syncRequest.organizationId, Transaction.cards.name, DateTimeUtil.utcLocalDateTimeToEpochMilliSecond(userSyncStatusLastCheckedAt))
        }

//        if (listCardsResponse.resultCodes.contains(ResultCode.EXTERNAL_SERVER_ERROR)){
//            TODO("result code handling")
//        }

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

        val cardHistoryEntity = cardMapper.toCardHistoryEntity(cardEntity) // modelMapper.map(cardEntity, CardHistoryEntity::class.java)
        cardHistoryRepository.save(cardHistoryEntity)
    }
}
