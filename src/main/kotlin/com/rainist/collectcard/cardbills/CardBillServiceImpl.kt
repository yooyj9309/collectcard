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
import com.rainist.collectcard.common.db.repository.CardBillTransactionRepository
import com.rainist.collectcard.common.db.repository.CardPaymentScheduledRepository
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.service.OrganizationService
import com.rainist.collectcard.common.service.UserSyncStatusService
import com.rainist.common.log.Log
import com.rainist.common.util.DateTimeUtil
import java.time.LocalDateTime
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CardBillServiceImpl(
    val headerService: HeaderService,
    val collectExecutorService: CollectExecutorService,
    val cardBillRepository: CardBillRepository,
    val cardBillHistoryRepository: CardBillHistoryRepository,
    val cardBillTransactionRepository: CardBillTransactionRepository,
    val cardPaymentScheduledRepository: CardPaymentScheduledRepository,
    val organizationService: OrganizationService,
    val userSyncStatusService: UserSyncStatusService
) : CardBillService {

    companion object : Log

    @Transactional
    override fun listUserCardBills(executionContext: CollectExecutionContext, startAt: Long?): ListCardBillsResponse {
        val now = DateTimeUtil.utcNowLocalDateTime()
        val banksaladUserId = executionContext.userId.toLong()

        /* request header */
        val header = headerService.makeHeader(executionContext.userId, executionContext.organizationId)

        /* request body */
        val request = ListCardBillsRequest().apply {
            dataBody = ListCardBillsRequestDataBody()
        }
        /* set startAt */
        val checkStartTime: LocalDateTime = startAt?.let {
            DateTimeUtil.epochMilliSecondToKSTLocalDateTime(startAt)
        } ?: kotlin.run {
            val maxMonth = organizationService.getOrganizationByOrganizationId(executionContext.organizationId).maxMonth
            DateTimeUtil.kstNowLocalDateTime().minusMonths(maxMonth.toLong())
        }

        executionContext.setStartAt(checkStartTime)

        // TODO 제거예정 diff확인용
        logger.info("CARDBILL_TIME_INFO $checkStartTime $startAt $banksaladUserId ")

        // 청구서 execution
        val cardBillsResponse = executeCardBill(executionContext, header, request)

        // 결제 예정 내역 execution
        val cardBillExpectedResponse = executeCardBillExpected(executionContext, header, request)

        // 청구서 DB IO
        cardBillsResponse.dataBody?.cardBills?.forEach { cardBill ->
            upsertCardBillAndTransactions(banksaladUserId, executionContext.organizationId, cardBill, now)
        }

        // 결제 예정 내역 DB IO
        cardBillExpectedResponse.dataBody?.cardBills?.map {
            deleteAndInsertCardBillExpectedTransactions(banksaladUserId, executionContext.organizationId, it.transactions ?: mutableListOf(), now)
        }

        val bills = mutableListOf<CardBill>()
        bills.addAll(cardBillExpectedResponse.dataBody?.cardBills ?: mutableListOf())
        bills.addAll(cardBillsResponse.dataBody?.cardBills ?: mutableListOf())

        // sort bill
        bills.sortByDescending { bill -> bill.paymentDay }
        bills.forEach { bill ->
            bill.transactions?.sortByDescending { transaction -> transaction.approvalDay }
        }

        return cardBillsResponse.apply {
            this.dataBody = ListCardBillsResponseDataBody().apply { this.cardBills = bills }
        }
    }

    private fun executeCardBill(executionContext: CollectExecutionContext, header: MutableMap<String, String?>, request: ListCardBillsRequest): ListCardBillsResponse {
        val cardBillsExecutionResponse: ExecutionResponse<ListCardBillsResponse> = collectExecutorService.execute(
            executionContext,
            Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.cardbills),
            ExecutionRequest.builder<ListCardBillsRequest>()
                .headers(header)
                .request(request)
                .build()
        )

        /* check response result */
//        ExecutionResponseValidator.validateResponseAndThrow(
//            cardBillsExecutionResponse,
//            cardBillsExecutionResponse.response.resultCodes
//        )
        return cardBillsExecutionResponse.response
    }

    private fun executeCardBillExpected(executionContext: CollectExecutionContext, header: MutableMap<String, String?>, request: ListCardBillsRequest): ListCardBillsResponse {
        val cardBillExpectedExecutionResponse: ExecutionResponse<ListCardBillsResponse> =
            collectExecutorService.execute(
                executionContext,
                Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.billTransactionExpected),
                ExecutionRequest.builder<ListCardBillsRequest>()
                    .headers(header)
                    .request(request)
                    .build()
            )

        /* check response result */
//        ExecutionResponseValidator.validateResponseAndThrow(
//            cardBillExpectedExecutionResponse,
//            cardBillExpectedExecutionResponse.response.resultCodes
//        )

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
                ).let { cardBillTransactionRepository.save(it) }
            }
            CardBillUtil.makeCardBillHistoryEntityFromCardBillHistory(newCardBillEntity).let {
                cardBillHistoryRepository.save(it)
            }
            return
        }

        // no changes
        if (newCardBillEntity.equal(oldCardBillEntity)) {
            return
        }

        // bill updated
        // delete transactions
        cardBillTransactionRepository.findAllByBilledYearMonthAndBanksaladUserIdAndCardCompanyCardIdAndBillNumber(
            oldCardBillEntity.billedYearMonth ?: "",
            banksaladUserId,
            organizationId,
            cardBill.billNumber
        )?.forEach { transacitonEntity ->
            transacitonEntity.isDeleted = true
            cardBillTransactionRepository.save(transacitonEntity)
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
            ).let { cardBillTransactionRepository.save(it) }
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
}
