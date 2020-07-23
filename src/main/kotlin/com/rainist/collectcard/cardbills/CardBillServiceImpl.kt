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
import com.rainist.collectcard.common.db.repository.CardBillRepository
import com.rainist.collectcard.common.exception.CollectcardException
import com.rainist.collectcard.common.service.ApiLogService
import com.rainist.collectcard.common.service.CardOrganization
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.service.OrganizationService
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
    val organizationService: OrganizationService
) : CardBillService {

    companion object : Log

    @Transactional
    override fun listUserCardBills(
        banksaladUserId: String,
        organization: CardOrganization,
        startAt: Long?
    ): ListCardBillsResponse {
        val header = headerService.makeHeader(banksaladUserId, organization)

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
            upsertCardBill(banksaladUserId, organization.organizationId, cardBill)
            cardBill.transactions?.map { billTransaction ->
                upsertCardBillTransaction(banksaladUserId, organization.organizationId, billTransaction)
            }
        }

        return cardBillResponse
    }

    private fun upsertCardBill(banksaladUserId: String, organizationId: String?, cardBill: CardBill) {
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
            this.prepaidAmount = cardBill.prepayedAmount
            this.paymentBankId = cardBill.paymentBankId
            this.paymentAccountNumber = cardBill.paymentAccountNumber
            this.totalPoint = cardBill.totalPoints?.toBigDecimal()
            this.expiringPoints = cardBill.expiringPoints?.toBigDecimal()
            this.lastCheckAt = DateTimeUtil.getLocalDateTime()
        }.let { cardBillRepository.save(it) }
    }

    private fun upsertCardBillTransaction(
        banksaladUserId: String,
        organizationId: String?,
        cardBillTransaction: CardBillTransaction
    ) {
        TODO()
    }

    private fun isCardBillUpdated(cardBill: CardBill, cardBillEntity: CardBillEntity): Boolean {
        cardBillEntity.also {
            if (cardBillEntity.userName != cardBill.userName) return true
            if (cardBillEntity.userGrade != cardBill.userGrade) return true
            if (cardBillEntity.paymentDay != cardBill.paymentDate) return true
            if (cardBillEntity.billedYearMonth != cardBill.billedYearMonth?.let { DateTimeUtil.zoneDateTimeToString(it) }) return true
            if (cardBillEntity.nextPaymentDay != cardBill.nextPaymentDate) return true
            if (cardBillEntity.billingAmount != cardBill.billingAmount) return true
            if (cardBillEntity.prepaidAmount != cardBill.prepayedAmount) return true
            if (cardBillEntity.paymentBankId != cardBill.paymentBankId) return true
            if (cardBillEntity.paymentAccountNumber != cardBill.paymentAccountNumber) return true
            if (cardBillEntity.totalPoint != cardBill.totalPoints?.toBigDecimal()) return true
            if (cardBillEntity.expiringPoints != cardBill.expiringPoints?.toBigDecimal()) return true
        }
        return false
    }
}
