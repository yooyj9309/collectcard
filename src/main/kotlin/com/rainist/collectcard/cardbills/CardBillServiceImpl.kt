package com.rainist.collectcard.cardbills

// import com.rainist.collectcard.common.util.ExecutionResponseValidator
import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.cardbills.dto.CardBill
import com.rainist.collectcard.cardbills.dto.CardBillTransaction
import com.rainist.collectcard.cardbills.dto.ListCardBillsRequest
import com.rainist.collectcard.cardbills.dto.ListCardBillsRequestDataBody
import com.rainist.collectcard.cardbills.dto.ListCardBillsResponse
import com.rainist.collectcard.cardbills.dto.ListCardBillsResponseDataBody
import com.rainist.collectcard.cardbills.util.CardBillUtil
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.common.db.repository.CardBillHistoryRepository
import com.rainist.collectcard.common.db.repository.CardBillRepository
import com.rainist.collectcard.common.db.repository.CardBillScheduledHistoryRepository
import com.rainist.collectcard.common.db.repository.CardBillScheduledRepository
import com.rainist.collectcard.common.db.repository.CardBillTransactionHistoryRepository
import com.rainist.collectcard.common.db.repository.CardBillTransactionRepository
import com.rainist.collectcard.common.db.repository.CardPaymentScheduledRepository
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.service.ExecutionResponseValidateService
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.service.OrganizationService
import com.rainist.collectcard.common.service.UserSyncStatusService
import com.rainist.collectcard.common.util.CustomStringUtil
import com.rainist.common.log.Log
import com.rainist.common.util.DateTimeUtil
import java.time.LocalDateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CardBillServiceImpl(
    val headerService: HeaderService,
    val collectExecutorService: CollectExecutorService,
    val cardBillRepository: CardBillRepository,
    val cardBillHistoryRepository: CardBillHistoryRepository,
    val cardBillScheduledRepository: CardBillScheduledRepository,
    val cardBillScheduledHistoryRepository: CardBillScheduledHistoryRepository,
    val cardBillTransactionRepository: CardBillTransactionRepository,
    val cardBillTransactionHistoryRepository: CardBillTransactionHistoryRepository,
    val cardPaymentScheduledRepository: CardPaymentScheduledRepository,
    val organizationService: OrganizationService,
    val userSyncStatusService: UserSyncStatusService,
    val executionResponseValidateService: ExecutionResponseValidateService,
    @Value("\${shinhancard.organizationId}") private var shinhancardOrganizationId: String
) : CardBillService {

    companion object : Log

    @Transactional
    override fun listUserCardBills(executionContext: CollectExecutionContext, now: LocalDateTime): ListCardBillsResponse {
        val banksaladUserId = executionContext.userId.toLong()
        val organizationId = executionContext.organizationId
        /* request header */
        val header = headerService.makeHeader(banksaladUserId.toString(), organizationId)

        /* request body */
        val request = ListCardBillsRequest().apply {
            dataBody = ListCardBillsRequestDataBody()
        }

        // 청구서 execution
        val cardBillsResponse = executeCardBill(executionContext, header, request, now)

        // 결제 예정 내역 execution
        val cardBillExpectedResponse = executeCardBillExpected(executionContext, header, request, banksaladUserId, now)

        // merge bill and bill expected
        val bills = mutableListOf<CardBill>()
        bills.addAll(cardBillExpectedResponse.dataBody?.cardBills ?: mutableListOf())
        bills.addAll(cardBillsResponse.dataBody?.cardBills ?: mutableListOf())

        // sort bill
        bills.sortByDescending { bill -> bill.paymentDay }
        bills.forEach { bill ->
            bill.transactions?.sortByDescending { transaction -> transaction.approvalDay }
        }

        // 신한카드 카드번호 masking 작업 진행.
        postProgress(organizationId, bills)

        return cardBillsResponse.apply {
            this.dataBody = ListCardBillsResponseDataBody().apply { this.cardBills = bills }
        }
    }

    private fun executeCardBill(executionContext: CollectExecutionContext, header: MutableMap<String, String?>, request: ListCardBillsRequest, now: LocalDateTime): ListCardBillsResponse {
        /* set startAt */
        val maxMonth = organizationService.getOrganizationByOrganizationId(executionContext.organizationId).maxMonth
        val defaultCheckStartTime = DateTimeUtil.kstNowLocalDateTime().minusMonths(maxMonth.toLong())

        val checkStartTime = userSyncStatusService.getUserSyncStatusLastCheckAt(
            executionContext.userId.toLong(), executionContext.organizationId, Transaction.cardbills.name
        )?.let { lastCheckAt ->
            DateTimeUtil.epochMilliSecondToKSTLocalDateTime(lastCheckAt)
        }?.takeIf {
            defaultCheckStartTime.isBefore(it)
        } ?: defaultCheckStartTime

        executionContext.setStartAt(checkStartTime)

        // TODO 제거예정 diff확인용
        logger.info("CARDBILL_TIME_INFO $checkStartTime ${executionContext.userId} ")

        val cardBillsExecutionResponse: ExecutionResponse<ListCardBillsResponse> = collectExecutorService.execute(
            executionContext,
            Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.cardbills),
            ExecutionRequest.builder<ListCardBillsRequest>()
                .headers(header)
                .request(request)
                .build()
        )

        // 청구서 DB IO
        cardBillsExecutionResponse.response.dataBody?.cardBills?.forEach { cardBill ->
            upsertCardBillAndTransactions(executionContext.userId.toLong(), executionContext.organizationId, cardBill, now)
        }

        userSyncStatusService.upsertUserSyncStatus(
            executionContext.userId.toLong(),
            executionContext.organizationId,
            Transaction.cardbills.name,
            DateTimeUtil.utcLocalDateTimeToEpochMilliSecond(now),
            executionResponseValidateService.validate(executionContext, cardBillsExecutionResponse)
        )

        return cardBillsExecutionResponse.response
    }

    private fun executeCardBillExpected(executionContext: CollectExecutionContext, header: MutableMap<String, String?>, request: ListCardBillsRequest, banksaladUserId: Long, now: LocalDateTime): ListCardBillsResponse {
        /* set startAt */
        val maxMonth = organizationService.getOrganizationByOrganizationId(executionContext.organizationId).maxMonth
        val defaultCheckStartTime = DateTimeUtil.kstNowLocalDateTime().minusMonths(maxMonth.toLong())

        val checkStartTime = userSyncStatusService.getUserSyncStatusLastCheckAt(
            executionContext.userId.toLong(), executionContext.organizationId, Transaction.billTransactionExpected.name
        )?.let { lastCheckAt ->
            DateTimeUtil.epochMilliSecondToKSTLocalDateTime(lastCheckAt)
        }?.takeIf {
            defaultCheckStartTime.isBefore(it)
        } ?: defaultCheckStartTime

        executionContext.setStartAt(checkStartTime)

        val cardBillExpectedExecutionResponse: ExecutionResponse<ListCardBillsResponse> =
            collectExecutorService.execute(
                executionContext,
                Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.billTransactionExpected),
                ExecutionRequest.builder<ListCardBillsRequest>()
                    .headers(header)
                    .request(request)
                    .build()
            )

        // 결제 예정 내역 DB IO
        cardBillExpectedExecutionResponse.response.dataBody?.cardBills?.map {
            upsertCardBillScheduled(banksaladUserId, executionContext.organizationId, it, now)
            deleteAndInsertCardBillExpectedTransactions(banksaladUserId, executionContext.organizationId, it.transactions ?: mutableListOf(), now)
        }

        userSyncStatusService.upsertUserSyncStatus(
            banksaladUserId,
            executionContext.organizationId,
            Transaction.billTransactionExpected.name,
            DateTimeUtil.utcLocalDateTimeToEpochMilliSecond(now),
            executionResponseValidateService.validate(executionContext, cardBillExpectedExecutionResponse)
        )

        return cardBillExpectedExecutionResponse.response
    }

    private fun upsertCardBillAndTransactions(banksaladUserId: Long, organizationId: String?, cardBill: CardBill, now: LocalDateTime) {
        val newCardBillEntity = CardBillUtil.makeCardBillEntity(banksaladUserId, organizationId ?: "", cardBill, now)
        val oldCardBillEntity = cardBillRepository.findByBanksaladUserIdAndCardCompanyIdAndBillNumberAndBillTypeAndCardType(
            banksaladUserId,
            organizationId ?: "",
            cardBill.billNumber ?: "",
            cardBill.billType ?: "",
            cardBill.cardType?.name ?: ""
        )

        // new
        // insert bill, transactions, history
        if (oldCardBillEntity == null) {
            cardBillRepository.save(newCardBillEntity)
            cardBill.transactions?.forEachIndexed { index, cardBillTransaction ->
                CardBillUtil.makeCardBillTransactionEntity(
                    banksaladUserId,
                    organizationId ?: "",
                    cardBill.billedYearMonth,
                    index,
                    cardBillTransaction,
                    now
                ).let {
                    val entity = cardBillTransactionRepository.save(it)
                    cardBillTransactionHistoryRepository.save(CardBillUtil.makeCardBillTransactionHistoryEntity(entity))
                }
            }
            CardBillUtil.makeCardBillHistoryEntityFromCardBillHistory(newCardBillEntity).let {
                cardBillHistoryRepository.save(it)
            }
            return
        }

        // no changes
        if (newCardBillEntity.equal(oldCardBillEntity)) {
            // last check at 만 update
            oldCardBillEntity.apply {
                lastCheckAt = now
            }.let {
                cardBillRepository.save(it)
            }
            cardBillTransactionRepository.findAllByBilledYearMonthAndBanksaladUserIdAndCardCompanyIdAndBillNumber(
                oldCardBillEntity.billedYearMonth ?: "",
                banksaladUserId,
                organizationId,
                cardBill.billNumber
            ).map { transacitonEntity ->
                transacitonEntity.apply {
                    lastCheckAt = now
                }
            }.let {
                cardBillTransactionRepository.saveAll(it)
            }
            return
        }

        // bill updated
        // delete transactions
        cardBillTransactionRepository.findAllByBilledYearMonthAndBanksaladUserIdAndCardCompanyIdAndBillNumber(
            oldCardBillEntity.billedYearMonth ?: "",
            banksaladUserId,
            organizationId,
            cardBill.billNumber
        ).forEach { transacitonEntity ->
            cardBillTransactionRepository.delete(transacitonEntity)
        }

        // update bill
        val billEntity = newCardBillEntity.apply {
            this.cardBillId = oldCardBillEntity.cardBillId
            this.createdAt = oldCardBillEntity.createdAt
            this.updatedAt = oldCardBillEntity.updatedAt
        }
        cardBillRepository.save(billEntity)

        // insert history
        CardBillUtil.makeCardBillHistoryEntityFromCardBillHistory(billEntity).let {
            cardBillHistoryRepository.save(it)
        }

        // insert new transactions
        cardBill.transactions?.forEachIndexed { index, cardBillTransaction ->
            CardBillUtil.makeCardBillTransactionEntity(
                banksaladUserId,
                organizationId ?: "",
                cardBill.billedYearMonth,
                index,
                cardBillTransaction,
                now
            ).let {
                val entity = cardBillTransactionRepository.save(it)
                cardBillTransactionHistoryRepository.save(CardBillUtil.makeCardBillTransactionHistoryEntity(entity))
            }
        }
    }

    private fun upsertCardBillScheduled(banksaladUserId: Long, organizationId: String?, cardBill: CardBill, now: LocalDateTime) {
        val newCardBillScheduledEntity = CardBillUtil.makeCardBillScheduledEntity(banksaladUserId, organizationId ?: "", cardBill, now)
        val oldCardBillScheduledEntity = cardBillScheduledRepository.findByBanksaladUserIdAndCardCompanyIdAndBillNumberAndBillTypeAndCardType(
            banksaladUserId,
            organizationId ?: "",
            cardBill.billNumber ?: "",
            cardBill.billType ?: "",
            cardBill.cardType?.name ?: ""
        )

        // new
        // insert bill scheduled, history
        if (oldCardBillScheduledEntity == null) {
            cardBillScheduledRepository.save(newCardBillScheduledEntity)
            CardBillUtil.makeCardBillScheduledHistoryEntityFromCardBillScheduledEntity(newCardBillScheduledEntity).let {
                cardBillScheduledHistoryRepository.save(it)
            }
            return
        }

        // no changes
        if (newCardBillScheduledEntity.equal(oldCardBillScheduledEntity)) {
            // last check at 만 update
            oldCardBillScheduledEntity.apply {
                lastCheckAt = now
            }.let {
                cardBillScheduledRepository.save(it)
            }
            return
        }

        // update bill scheduled
        val billEntity = newCardBillScheduledEntity.apply {
            this.cardBillScheduledId = oldCardBillScheduledEntity.cardBillScheduledId
            this.createdAt = oldCardBillScheduledEntity.createdAt
            this.updatedAt = oldCardBillScheduledEntity.updatedAt
        }
        cardBillScheduledRepository.save(billEntity)

        // insert history
        CardBillUtil.makeCardBillScheduledHistoryEntityFromCardBillScheduledEntity(billEntity).let {
            cardBillScheduledHistoryRepository.save(it)
        }
    }

    private fun deleteAndInsertCardBillExpectedTransactions(
        banksaladUserId: Long,
        organizationId: String?,
        cardBillTransactionExpected: List<CardBillTransaction>,
        now: LocalDateTime
    ) {
        cardPaymentScheduledRepository.deleteAllByBanksaladUserIdAndCardCompanyId(banksaladUserId, organizationId)

        cardBillTransactionExpected.forEachIndexed { index, cardBillTransaction ->
            CardBillUtil.makeCardPaymentScheduledEntity(
                banksaladUserId,
                organizationId ?: "",
                index,
                cardBillTransaction,
                now
            ).let {
                cardPaymentScheduledRepository.save(it)
            }
        }
    }

    fun postProgress(organizationId: String, bills: MutableList<CardBill>) {
        when (organizationId) {
            shinhancardOrganizationId -> {
                bills.map { cardBill ->
                    cardBill.transactions?.map { cardBillTransaction ->
                        cardBillTransaction.cardNumber = CustomStringUtil.replaceNumberToMask(cardBillTransaction.cardNumber)
                        cardBillTransaction.cardNumberMasked = CustomStringUtil.replaceNumberToMask(cardBillTransaction.cardNumberMasked)
                    }
                }
            }
            else -> {
            }
        }
    }
}
