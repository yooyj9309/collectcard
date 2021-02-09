package com.rainist.collectcard.common.publish

import com.rainist.collectcard.cardbills.CardBillService
import com.rainist.collectcard.cardbills.dto.CardBill
import com.rainist.collectcard.cardbills.dto.CardBillTransaction
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.publish.banksalad.CardBillPublishService
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.service.UserSyncStatusService
import com.rainist.collectcard.common.util.ExecutionTestUtil
import com.rainist.common.log.Log
import com.rainist.common.util.DateTimeUtil
import java.time.LocalDateTime
import javax.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestTemplate

@ExtendWith(SpringExtension::class)
@SpringBootTest
@DisplayName("카드 청구서 publish 테스트")
class CardBillPublishTest {

    @Autowired
    lateinit var cardBillPublishService: CardBillPublishService

    @Autowired
    lateinit var commonRestTemplate: RestTemplate

    @Autowired
    lateinit var cardBillService: CardBillService

    @Autowired
    lateinit var userSyncStatusService: UserSyncStatusService

    @MockBean
    lateinit var headerService: HeaderService

    val userId = 1L
    val organizationId = "shinhancard"

    companion object : Log

    @Test
    @Rollback
    @Transactional
    fun cardBillShadowingTest() {
        val now = DateTimeUtil.utcNowLocalDateTime()
        val executionContext = ExecutionTestUtil.getExecutionContext("1", "shinhancard", now) as CollectExecutionContext
        given(headerService.makeHeader(executionContext.userId, executionContext.organizationId))
            .willReturn(
                mutableMapOf(
                    "contentType" to MediaType.APPLICATION_JSON_VALUE,
                    "authorization" to "Bearer 123",
                    "clientId" to "596d66692c4069c168b57c59"
                )
            )

        userSyncStatusService.upsertUserSyncStatus(
            executionContext.userId.toLong(),
            executionContext.organizationId,
            Transaction.cardbills.name,
            DateTimeUtil.kstLocalDateTimeToEpochMilliSecond(LocalDateTime.of(2020, 7, 30, 0, 0)),
            true
        )

        setupServerPaging()
        val response = cardBillService.listUserCardBills(executionContext, now)

        val shadowingResponse = cardBillPublishService.shadowing(
            userId,
            organizationId,
            now,
            executionContext.executionRequestId,
            response
        )

        val listSize = shadowingResponse.oldList.size
        val oldBills = shadowingResponse.oldList as List<CardBill>
        val shadowingBills = shadowingResponse.dbList as List<CardBill>

        for (i in 0 until listSize) {
            // bill 비교
            val isEqualBill = isEqualbill(oldBills, i, shadowingBills)
            val transactionSize = oldBills[i].transactions?.size

            for (j in 0 until transactionSize!!) {
                // cardbilltransaction 비교
                val isEqualBillTransaction =
                    isEqualbillTransaction(
                        oldBills[i].transactions?.get(j),
                        i,
                        shadowingBills[i].transactions?.get(j)
                    )
                assertThat(isEqualBillTransaction).isEqualTo(true)
            }
            assertThat(isEqualBill).isEqualTo(true)
        }
    }

    // cardBillTransactions 43개 필드 비교
    private fun isEqualbillTransaction(
        oldTransactions: CardBillTransaction?,
        i: Int,
        shadowingTransactions: CardBillTransaction?
    ): Boolean {
        val isDiffObject = listOf(
            oldTransactions?.cardTransactionId == shadowingTransactions?.cardTransactionId,
            oldTransactions?.cardCompanyCardId == shadowingTransactions?.cardCompanyCardId,
            oldTransactions?.cardNumber == shadowingTransactions?.cardNumber,
            oldTransactions?.cardNumberMasked == shadowingTransactions?.cardNumberMasked,
            oldTransactions?.businessLicenseNumber == shadowingTransactions?.businessLicenseNumber,
            oldTransactions?.storeName == shadowingTransactions?.storeName,
            oldTransactions?.storeNumber == shadowingTransactions?.storeNumber,
            oldTransactions?.cardType == shadowingTransactions?.cardType,
            oldTransactions?.cardTypeOrigin == shadowingTransactions?.cardTypeOrigin,
            oldTransactions?.cardTransactionType == shadowingTransactions?.cardTransactionType,
            oldTransactions?.cardTransactionTypeOrigin == shadowingTransactions?.cardTransactionTypeOrigin,
            oldTransactions?.currencyCode == shadowingTransactions?.currencyCode,
            oldTransactions?.isInstallmentPayment == shadowingTransactions?.isInstallmentPayment,
            oldTransactions?.installment == shadowingTransactions?.installment,
            oldTransactions?.installmentRound == shadowingTransactions?.installmentRound,
            oldTransactions?.netSalesAmount == shadowingTransactions?.netSalesAmount,
            oldTransactions?.serviceChargeAmount == shadowingTransactions?.serviceChargeAmount,
            oldTransactions?.tax == shadowingTransactions?.tax,
            oldTransactions?.paidPoints == shadowingTransactions?.paidPoints,
            oldTransactions?.isPointPay == shadowingTransactions?.isPointPay,
            oldTransactions?.discountAmount == shadowingTransactions?.discountAmount,
            oldTransactions?.amount == shadowingTransactions?.amount,
            oldTransactions?.canceledAmount == shadowingTransactions?.canceledAmount,
            oldTransactions?.approvalNumber == shadowingTransactions?.approvalNumber,
            oldTransactions?.approvalDay == shadowingTransactions?.approvalDay,
            oldTransactions?.approvalTime == shadowingTransactions?.approvalTime,
            oldTransactions?.pointsToEarn == shadowingTransactions?.pointsToEarn,
            oldTransactions?.isOverseaUse == shadowingTransactions?.isOverseaUse,
            oldTransactions?.paymentDay == shadowingTransactions?.paymentDay,
            oldTransactions?.storeCategory == shadowingTransactions?.storeCategory,
            oldTransactions?.storeCategoryOrigin == shadowingTransactions?.storeCategoryOrigin,
            oldTransactions?.transactionCountry == shadowingTransactions?.transactionCountry,
            oldTransactions?.billingRound == shadowingTransactions?.billingRound,
            oldTransactions?.paidAmount == shadowingTransactions?.paidAmount,
            oldTransactions?.billedAmount == shadowingTransactions?.billedAmount,
            oldTransactions?.billedFee == shadowingTransactions?.billedFee,
            oldTransactions?.remainingAmount == shadowingTransactions?.remainingAmount,
            oldTransactions?.isPaidFull == shadowingTransactions?.isPaidFull,
            oldTransactions?.cashback == shadowingTransactions?.cashback,
            oldTransactions?.pointsRate == shadowingTransactions?.pointsRate,
            oldTransactions?.billNumber == shadowingTransactions?.billNumber,
            oldTransactions?.billType == shadowingTransactions?.billType

        ).all { it }
        return isDiffObject
    }

    // cardbill 15개 필드 비교
    private fun isEqualbill(
        oldTransactions: List<CardBill>,
        i: Int,
        shadowingTransactions: List<CardBill>
    ): Boolean {
        return listOf(
            oldTransactions[i].billId == shadowingTransactions[i].billId,
            oldTransactions[i].billNumber == shadowingTransactions[i].billNumber,
            oldTransactions[i].billType == shadowingTransactions[i].billType,
            oldTransactions[i].cardType == shadowingTransactions[i].cardType,
            oldTransactions[i].userName == shadowingTransactions[i].userName,
            oldTransactions[i].userGrade == shadowingTransactions[i].userGrade,
            oldTransactions[i].userGradeOrigin == shadowingTransactions[i].userGradeOrigin,
            oldTransactions[i].paymentDay == shadowingTransactions[i].paymentDay,
            oldTransactions[i].billedYearMonth == shadowingTransactions[i].billedYearMonth,
            oldTransactions[i].nextPaymentDay == shadowingTransactions[i].nextPaymentDay,
            oldTransactions[i].billingAmount == shadowingTransactions[i].billingAmount,
            oldTransactions[i].prepaidAmount == shadowingTransactions[i].prepaidAmount,
            oldTransactions[i].paymentBankId == shadowingTransactions[i].paymentBankId,
            oldTransactions[i].paymentAccountNumber == shadowingTransactions[i].paymentAccountNumber,
            oldTransactions[i].totalPoints == shadowingTransactions[i].totalPoints,
            oldTransactions[i].expiringPoints == shadowingTransactions[i].expiringPoints
        ).all { it }
    }

    private fun setupServerPaging() {
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_check_bills,
            "classpath:mock/shinhancard/bill/bill_check_expected_1.json"
        )
        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_credit_bills,
            "classpath:mock/shinhancard/bill/bill_credit_expected_1.json"
        )

        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_check_bill_transactions,
            "classpath:mock/shinhancard/bill/bill_check_detail_expected_1.json"
        )
        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_credit_bill_transactions,
            "classpath:mock/shinhancard/bill/bill_credit_detail_expected_1.json"
        )

        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_list_user_card_bills_expected,
            "classpath:mock/shinhancard/bill/bill_transactions_expected_1.json"
        )
        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_lump_sum,
            "classpath:mock/shinhancard/bill/bill_transactions_expected_detail_lump_sum_p1.json"
        )
        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_lump_sum,
            "classpath:mock/shinhancard/bill/bill_transactions_expected_detail_lump_sum_p2.json"
        )

        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_list_user_card_bills_expected,
            "classpath:mock/shinhancard/bill/bill_transactions_expected_1.json"
        )
        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_installment,
            "classpath:mock/shinhancard/bill/bill_transactions_expected_detail_installment_p1.json"
        )
        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_installment,
            "classpath:mock/shinhancard/bill/bill_transactions_expected_detail_installment_p2.json"
        )
    }
}
