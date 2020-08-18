package com.rainist.collectcard.cardbills

import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.cardbills.dto.CardBill
import com.rainist.collectcard.cardbills.dto.CardBillTransaction
import com.rainist.collectcard.cardbills.dto.ListCardBillsRequest
import com.rainist.collectcard.cardbills.dto.ListCardBillsRequestDataBody
import com.rainist.collectcard.cardbills.dto.ListCardBillsResponse
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.common.db.entity.CardBillEntity
import com.rainist.collectcard.common.db.entity.CardBillTransactionEntity
import com.rainist.collectcard.common.db.entity.CardPaymentScheduledEntity
import com.rainist.collectcard.common.db.repository.CardBillRepository
import com.rainist.collectcard.common.db.repository.CardBillTransactionRepository
import com.rainist.collectcard.common.db.repository.CardPaymentScheduledRepository
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.util.SyncStatus
import com.rainist.common.log.Log
import com.rainist.common.util.DateTimeUtil
import java.math.BigDecimal
import java.time.LocalDateTime
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CardBillServiceImpl(
    val headerService: HeaderService,
    val collectExecutorService: CollectExecutorService,
    val cardBillRepository: CardBillRepository,
    val cardBillTransactionRepository: CardBillTransactionRepository,
    val cardPaymentScheduledRepository: CardPaymentScheduledRepository
) : CardBillService {

    companion object : Log {
        // TODO 해당부분 추후 organization 에서 상수를 전부 관리하는 형태로 변경 ( bill, transaction)
        const val DEFAULT_MAX_BILL_MONTH = 6L
    }

    @Transactional
    @SyncStatus(transactionId = "cardbills")
    override fun listUserCardBills(
        executionContext: CollectExecutionContext,
        startAt: Long?
    ): ListCardBillsResponse {

        val banksaladUserId = executionContext.userId.toLong()

        /* request header */
        val header = headerService.makeHeader(executionContext.userId, executionContext.organizationId)

        /* request body */
        /* set startAt */
        val checkStartTime: LocalDateTime = startAt?.let {
            DateTimeUtil.epochMilliSecondToKSTLocalDateTime(startAt)
        } ?: DateTimeUtil.kstNowLocalDateTime().minusMonths(DEFAULT_MAX_BILL_MONTH)

        executionContext.setStartAt(checkStartTime)
        val now = DateTimeUtil.utcNowLocalDateTime()

        // TODO 제거예정 diff확인용
        logger.info("CARDBILL_TIME_INFO $checkStartTime $startAt $banksaladUserId ")

        val request = ListCardBillsRequest().apply {
            dataBody = ListCardBillsRequestDataBody()
        }

        // 청구서 execution
        val cardBillsExecutionResponse: ExecutionResponse<ListCardBillsResponse> = collectExecutorService.execute(
            executionContext,
            Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.cardbills),
            ExecutionRequest.builder<ListCardBillsRequest>()
                .headers(header)
                .request(request)
                .build()
        )

        val cardBillsResponse = cardBillsExecutionResponse.response

        // 청구서 IO
        cardBillsResponse?.dataBody?.cardBills?.forEach { cardBill ->
            upsertCardBillAndTransactions(banksaladUserId, executionContext.organizationId, cardBill)
        }

        // 결제 예정 내역 execution
        val cardBillExpectedExecutionResponse: ExecutionResponse<ListCardBillsResponse> =
            collectExecutorService.execute(
                executionContext,
                Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.billTransactionExpected),
                ExecutionRequest.builder<ListCardBillsRequest>()
                    .headers(header)
                    .request(request)
                    .build()
            )

        val cardBillExpectedResponse = cardBillExpectedExecutionResponse.response

        // 결제 예정 내역 DB IO
        cardBillExpectedResponse?.dataBody?.cardBills?.flatMap {
            it.transactions ?: mutableListOf()
        }?.let {
            deleteAndInsertCardBillExpectedTransactions(banksaladUserId, executionContext.organizationId, it)
        }

        // merge 청구서, 결제 예정 내역
        cardBillsResponse?.dataBody?.cardBills?.addAll(
            0,
            cardBillExpectedResponse?.dataBody?.cardBills ?: mutableListOf()
        )

        // 1depth sorting
        cardBillsResponse?.dataBody?.cardBills?.sortByDescending { cardBill -> cardBill.paymentDay }

        // 2depth sorting
        cardBillsResponse?.dataBody?.cardBills?.forEach { cardBill ->
            cardBill.transactions?.sortByDescending { transaction -> transaction.approvalDay }
        }

        // TODO : validate Execution Response

        return cardBillsResponse
    }

    private fun upsertCardBillAndTransactions(banksaladUserId: Long, organizationId: String?, cardBill: CardBill) {
        val cardBillEntity = cardBillRepository.findByBanksaladUserIdAndCardCompanyIdAndBillNumber(
            banksaladUserId,
            organizationId ?: "",
            cardBill.billNumber ?: ""
        ) ?: CardBillEntity()

        if (isCardBillUpdated(cardBill, cardBillEntity)) {
            return
        }

        cardBillEntity.apply {
            this.banksaladUserId = banksaladUserId.toLong()
            this.cardCompanyId = organizationId
            this.billNumber = cardBill.billNumber
            this.lastCheckAt = DateTimeUtil.getLocalDateTime()
            this.userName = cardBill.userName
            this.userGrade = cardBill.userGrade
            this.userGradeOrigin = cardBill.userGradeOrigin
            this.paymentDay = cardBill.paymentDay ?: ""
            this.billedYearMonth = cardBill.billedYearMonth?.let { DateTimeUtil.zoneDateTimeToString(it) } ?: ""
            this.nextPaymentDay = cardBill.nextPaymentDay
            this.billingAmount = cardBill.billingAmount ?: BigDecimal("0.0000")
            this.prepaidAmount = cardBill.prepaidAmount ?: BigDecimal("0.0000")
            this.paymentBankId = cardBill.paymentBankId
            this.paymentAccountNumber = cardBill.paymentAccountNumber
            this.totalPoint = cardBill.totalPoints?.toBigDecimal()
            this.expiringPoints = cardBill.expiringPoints?.toBigDecimal()
        }.let { cardBillRepository.save(it) }

        cardBillTransactionRepository.deleteAllByBilledYearMonthAndBanksaladUserIdAndCardCompanyCardIdAndBillNumber(
            cardBillEntity.billedYearMonth ?: "",
            banksaladUserId,
            organizationId,
            cardBill.billNumber
        )

        cardBill.transactions?.forEachIndexed { index, cardBillTransaction ->
            insertCardBillTransaction(
                banksaladUserId,
                cardBillEntity.billedYearMonth,
                organizationId,
                index,
                cardBillTransaction
            )
        }
    }

    private fun insertCardBillTransaction(
        banksaladUserId: Long,
        billedYearMonth: String?,
        organizationId: String?,
        cardBillTransactionNo: Int?,
        cardBillTransaction: CardBillTransaction
    ) {
        CardBillTransactionEntity().apply {
            this.banksaladUserId = banksaladUserId
            this.billedYearMonth = billedYearMonth ?: ""
            this.cardCompanyId = organizationId
            this.billNumber = cardBillTransaction.billNumber
            this.cardBillTransactionNo = cardBillTransactionNo
            this.cardCompanyCardId = cardBillTransaction.cardCompanyCardId ?: ""
            this.cardName = cardBillTransaction.cardName
            this.cardNumber = cardBillTransaction.cardNumber?.replace("-", "")?.trim()
            this.cardNumberMask = cardBillTransaction.cardNumberMasked?.replace("-", "")?.trim()
            this.businessLicenseNumber = cardBillTransaction.businessLicenseNumber
            this.storeName = cardBillTransaction.storeName
            this.storeNumber = cardBillTransaction.storeNumber
            this.cardType = cardBillTransaction.cardType?.name
            this.cardTypeOrigin = cardBillTransaction.cardTypeOrigin
            this.cardTransactionType = cardBillTransaction.cardTransactionType?.name
            this.cardTransactionTypeOrigin = cardBillTransaction.cardTransactionTypeOrigin
            this.currencyCode = cardBillTransaction.currencyCode
            this.isInstallmentPayment = cardBillTransaction.isInstallmentPayment ?: false
            this.installment = cardBillTransaction.installment ?: 0
            this.installmentRound = cardBillTransaction.installmentRound
            this.netSalesAmount = cardBillTransaction.netSalesAmount ?: BigDecimal("0.0000")
            this.serviceChargeAmount = cardBillTransaction.serviceChargeAmount
            this.taxAmount = cardBillTransaction.tax
            this.paidPoints = cardBillTransaction.paidPoints
            this.isPointPay = cardBillTransaction.isPointPay
            this.discountAmount = cardBillTransaction.discountAmount
            this.canceledAmount = cardBillTransaction.canceledAmount
            this.approvalNumber = cardBillTransaction.approvalNumber ?: ""
            this.approvalDay = cardBillTransaction.approvalDay ?: ""
            this.approvalTime = cardBillTransaction.approvalTime ?: ""
            this.pointsToEarn = cardBillTransaction.pointsToEarn
            this.isOverseaUse = cardBillTransaction.isOverseaUse ?: false
            this.paymentDay = cardBillTransaction.paymentDay ?: ""
            this.storeCategory = cardBillTransaction.storeCategory
            this.storeCategoryOrigin = cardBillTransaction.storeCategoryOrigin
            this.transactionCountry = cardBillTransaction.transactionCountry
            this.billingRound = cardBillTransaction.billingRound
            this.paidAmount = cardBillTransaction.paidAmount
            this.billedAmount = cardBillTransaction.billedAmount
            this.billedFee = cardBillTransaction.billedFee
            this.remainingAmount = cardBillTransaction.remainingAmount
            this.isPaidFull = cardBillTransaction.isPaidFull
            this.cashbackAmount = cardBillTransaction.cashback
            this.pointsRate = cardBillTransaction.pointsRate
            this.lastCheckAt = DateTimeUtil.getLocalDateTime()
        }.let {
            cardBillTransactionRepository.save(it)
        }
    }

    private fun isCardBillUpdated(cardBill: CardBill, cardBillEntity: CardBillEntity): Boolean {
        cardBillEntity.also {
            if (cardBillEntity.userName != cardBill.userName) return true
            if (cardBillEntity.userGrade != cardBill.userGrade) return true
            if (cardBillEntity.paymentDay != cardBill.paymentDay) return true
            if (cardBillEntity.billedYearMonth != cardBill.billedYearMonth?.let { DateTimeUtil.zoneDateTimeToString(it) }) return true
            if (cardBillEntity.nextPaymentDay != cardBill.nextPaymentDay) return true
            if (cardBillEntity.billingAmount != cardBill.billingAmount) return true
            if (cardBillEntity.prepaidAmount != cardBill.prepaidAmount) return true
            if (cardBillEntity.paymentBankId != cardBill.paymentBankId) return true
            if (cardBillEntity.paymentAccountNumber != cardBill.paymentAccountNumber) return true
            if (cardBillEntity.totalPoint != cardBill.totalPoints?.toBigDecimal()) return true
            if (cardBillEntity.expiringPoints != cardBill.expiringPoints?.toBigDecimal()) return true
        }
        return false
    }

    private fun deleteAndInsertCardBillExpectedTransactions(
        banksaladUserId: Long,
        organizationId: String?,
        cardBillTransactionExpected: List<CardBillTransaction>
    ) {
        cardPaymentScheduledRepository.deleteAllByBanksaladUserIdAndCardCompanyId(banksaladUserId, organizationId)

        cardBillTransactionExpected.forEachIndexed { index, cardBillTransaction ->
            insertCardBillExpectedTransaction(banksaladUserId, organizationId, index, cardBillTransaction)
        }
    }

    private fun insertCardBillExpectedTransaction(
        banksaladUserId: Long,
        organizationId: String?,
        paymentScheduledTransactionNo: Int?,
        cardBillTransaction: CardBillTransaction
    ) {

        CardPaymentScheduledEntity().apply {
            this.banksaladUserId = banksaladUserId.toLong()
            this.cardCompanyId = organizationId
            this.paymentScheduledTransactionNo = paymentScheduledTransactionNo
            this.cardCompanyCardId = cardBillTransaction.cardCompanyCardId ?: ""
            this.cardName = cardBillTransaction.cardName
            this.cardNumber = cardBillTransaction.cardNumber?.replace("-", "")?.trim()
            this.cardNumberMask = cardBillTransaction.cardNumberMasked?.replace("-", "")?.trim()
            this.businessLicenseNumber = cardBillTransaction.businessLicenseNumber
            this.storeName = cardBillTransaction.storeName
            this.storeNumber = cardBillTransaction.storeNumber
            this.cardType = cardBillTransaction.cardType?.name
            this.cardTypeOrigin = cardBillTransaction.cardTypeOrigin
            this.cardTransactionType = cardBillTransaction.cardTransactionType?.name
            this.cardTransactionTypeOrigin = cardBillTransaction.cardTransactionTypeOrigin
            this.currencyCode = cardBillTransaction.currencyCode
            this.isInstallmentPayment = cardBillTransaction.isInstallmentPayment ?: false
            this.installment = cardBillTransaction.installment ?: 0
            this.installmentRound = cardBillTransaction.installmentRound
            this.netSalesAmount = cardBillTransaction.netSalesAmount ?: BigDecimal("0.0000")
            this.serviceChargeAmount = cardBillTransaction.serviceChargeAmount
            this.taxAmount = cardBillTransaction.tax
            this.paidPoints = cardBillTransaction.paidPoints
            this.isPointPay = cardBillTransaction.isPointPay
            this.discountAmount = cardBillTransaction.discountAmount
            this.canceledAmount = cardBillTransaction.canceledAmount
            this.approvalNumber = cardBillTransaction.approvalNumber ?: ""
            this.approvalDay = cardBillTransaction.approvalDay ?: ""
            this.approvalTime = cardBillTransaction.approvalTime ?: ""
            this.pointsToEarn = cardBillTransaction.pointsToEarn
            this.isOverseaUse = cardBillTransaction.isOverseaUse ?: false
            this.paymentDay = cardBillTransaction.paymentDay
            this.storeCategory = cardBillTransaction.storeCategory
            this.storeCategoryOrigin = cardBillTransaction.storeCategoryOrigin
            this.transactionCountry = cardBillTransaction.transactionCountry
            this.billingRound = cardBillTransaction.billingRound
            this.paidAmount = cardBillTransaction.paidAmount
            this.billedAmount = cardBillTransaction.billedAmount
            this.billedFee = cardBillTransaction.billedFee
            this.remainingAmount = cardBillTransaction.remainingAmount
            this.isPaidFull = cardBillTransaction.isPaidFull
            this.cashbackAmount = cardBillTransaction.cashback
            this.pointsRate = cardBillTransaction.pointsRate
            this.lastCheckAt = DateTimeUtil.getLocalDateTime()
            this.lastCheckAt = DateTimeUtil.getLocalDateTime()
        }.let {
            cardPaymentScheduledRepository.save(it)
        }
    }
}
