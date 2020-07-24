package com.rainist.collectcard.cardbills

import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.ApiLog
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
import com.rainist.collectcard.common.db.repository.CardBillRepository
import com.rainist.collectcard.common.db.repository.CardBillTransactionRepository
import com.rainist.collectcard.common.exception.CollectcardException
import com.rainist.collectcard.common.service.ApiLogService
import com.rainist.collectcard.common.service.CardOrganization
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.common.log.Log
import com.rainist.common.util.DateTimeUtil
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CardBillServiceImpl(
    val apiLogService: ApiLogService,
    val headerService: HeaderService,
    val collectExecutorService: CollectExecutorService,
    val cardBillRepository: CardBillRepository,
    val cardBillTransactionRepository: CardBillTransactionRepository
) : CardBillService {

    companion object : Log

    @Transactional
    override fun listUserCardBills(
        banksaladUserId: String,
        organization: CardOrganization,
        startAt: Long?
    ): ListCardBillsResponse {
        val header = headerService.makeHeader(banksaladUserId, organization.organizationId ?: "")

        val request = ListCardBillsRequest().apply {
            dataBody = ListCardBillsRequestDataBody().apply {
                this.startAt = startAt?.let {
                    DateTimeUtil.epochMilliSecondToKSTLocalDateTime(startAt)
                }?.let {
                    DateTimeUtil.localDatetimeToString(it, "yyyyMMdd")
                } ?: DateTimeUtil.kstNowLocalDate()
                    .minusMonths(organization.maxMonth.toLong())
                    .let { DateTimeUtil.localDateToString(it, "yyyyMMdd") }
            }
        }

        val executionResponse: ExecutionResponse<ListCardBillsResponse> = collectExecutorService.execute(
            Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.billTransactionExpected),
            ExecutionRequest.builder<ListCardBillsRequest>()
                .headers(header)
                .request(request)
                .build(),
            { apiLog: ApiLog ->
                apiLogService.logRequest(organization.organizationId ?: "", banksaladUserId.toLong(), apiLog)
            },
            { apiLog: ApiLog ->
                apiLogService.logResponse(organization.organizationId ?: "", banksaladUserId.toLong(), apiLog)
            }
        )

        // TODO : error handling
        if (!HttpStatus.valueOf(executionResponse.httpStatusCode).is2xxSuccessful) {
            throw CollectcardException("Resopnse status is not success")
        }

        val cardBillResponse = executionResponse.response

        cardBillResponse.dataBody?.cardBills?.forEach { cardBill ->
            upsertCardBillAndTransactions(banksaladUserId, organization.organizationId, cardBill)
        }

        return cardBillResponse
    }

    private fun upsertCardBillAndTransactions(banksaladUserId: String, organizationId: String?, cardBill: CardBill) {
        val cardBillEntity = cardBillRepository.findByBanksaladUserIdAndCardCompanyIdAndBillNumber(
            banksaladUserId.toLong(),
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
            this.userName = cardBill.userName
            this.userGrade = cardBill.userGrade
            this.paymentDay = cardBill.paymentDate
            this.billedYearMonth = cardBill.billedYearMonth?.let { DateTimeUtil.zoneDateTimeToString(it) }
            this.nextPaymentDay = cardBill.nextPaymentDate
            this.billingAmount = cardBill.billingAmount
            this.prepaidAmount = cardBill.prepaidAmount
            this.paymentBankId = cardBill.paymentBankId
            this.paymentAccountNumber = cardBill.paymentAccountNumber
            this.totalPoint = cardBill.totalPoints?.toBigDecimal()
            this.expiringPoints = cardBill.expiringPoints?.toBigDecimal()
            this.lastCheckAt = DateTimeUtil.getLocalDateTime()
        }.let { cardBillRepository.save(it) }

        cardBillTransactionRepository.deleteAllByBanksaladUserIdAndCardCompanyCardIdAndBillNumber(
            banksaladUserId.toLong(),
            organizationId,
            cardBill.billNumber
        )

        cardBill.transactions?.forEachIndexed { index, cardBillTransaction ->
            insertCardBillTransaction(
                banksaladUserId, organizationId, index, cardBillTransaction
            )
        }
    }

    private fun insertCardBillTransaction(banksaladUserId: String, organizationId: String?, cardBillTransactionNo: Int?, cardBillTransaction: CardBillTransaction) {
        CardBillTransactionEntity().apply {
            this.banksaladUserId = banksaladUserId.toLong()
            this.cardCompanyId = organizationId
            this.billNumber = cardBillTransaction.billNumber
            this.cardBillTransactionNo = cardBillTransactionNo
//            this.cardCompanyCardId = cardBillTransaction.
            this.cardName = cardBillTransaction.cardName
            this.cardNumber = cardBillTransaction.cardNumber
            this.cardNumberMask = cardBillTransaction.cardNumberMasked
            this.businessLicenseNumber = cardBillTransaction.businessLicenseNumber
            this.storeName = cardBillTransaction.storeName
            this.storeNumber = cardBillTransaction.storeNumber
            this.cardType = cardBillTransaction.cardType
            this.cardTypeOrigin = cardBillTransaction.cardTypeOrigin
            this.cardTransactionType = cardBillTransaction.cardTransactionType?.name
            this.cardTransactionTypeOrigin = cardBillTransaction.cardTransactionTypeOrigin
            this.currencyCode = cardBillTransaction.currencyCode
            this.isInstallmentPayment = cardBillTransaction.isInstallmentPayment
            this.installment = cardBillTransaction.installment
            this.installmentRound = cardBillTransaction.installmentRound
            this.netSalesAmount = cardBillTransaction.netSalesAmount
            this.serviceChargeAmount = cardBillTransaction.serviceChargeAmount
            this.taxAmount = cardBillTransaction.tax
            this.paidPoints = cardBillTransaction.paidPoints
            this.isPointPay = cardBillTransaction.isPointPay
            this.discountAmount = cardBillTransaction.discountAmount
            this.canceledAmount = cardBillTransaction.canceledAmount
            this.approvalNumber = cardBillTransaction.approvalNumber
            this.approvalDay = cardBillTransaction.approvalDay
            this.approvalTime = cardBillTransaction.approvalTime
            this.pointsToEarn = cardBillTransaction.pointsToEarn
            this.isOverseaUse = cardBillTransaction.isOverseaUse
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
        }.let {
            cardBillTransactionRepository.save(it)
        }
    }

    private fun isCardBillUpdated(cardBill: CardBill, cardBillEntity: CardBillEntity): Boolean {
        cardBillEntity.also {
            if (cardBillEntity.userName != cardBill.userName) return true
            if (cardBillEntity.userGrade != cardBill.userGrade) return true
            if (cardBillEntity.paymentDay != cardBill.paymentDate) return true
            if (cardBillEntity.billedYearMonth != cardBill.billedYearMonth?.let { DateTimeUtil.zoneDateTimeToString(it) }) return true
            if (cardBillEntity.nextPaymentDay != cardBill.nextPaymentDate) return true
            if (cardBillEntity.billingAmount != cardBill.billingAmount) return true
            if (cardBillEntity.prepaidAmount != cardBill.prepaidAmount) return true
            if (cardBillEntity.paymentBankId != cardBill.paymentBankId) return true
            if (cardBillEntity.paymentAccountNumber != cardBill.paymentAccountNumber) return true
            if (cardBillEntity.totalPoint != cardBill.totalPoints?.toBigDecimal()) return true
            if (cardBillEntity.expiringPoints != cardBill.expiringPoints?.toBigDecimal()) return true
        }
        return false
    }
}
