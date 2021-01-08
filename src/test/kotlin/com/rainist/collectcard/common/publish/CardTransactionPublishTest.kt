package com.rainist.collectcard.common.publish

import com.rainist.collectcard.card.CardService
import com.rainist.collectcard.cardtransactions.CardTransactionService
import com.rainist.collectcard.cardtransactions.dto.CardTransaction
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.publish.banksalad.CardTransactionPublishService
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.service.UserSyncStatusService
import com.rainist.collectcard.common.util.ExecutionTestUtil
import com.rainist.common.util.DateTimeUtil
import java.util.UUID
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
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
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@DisplayName("카드 이용내역 publish 테스트")
class CardTransactionPublishTest {
    @Autowired
    lateinit var cardTransactionPublishService: CardTransactionPublishService

    @Autowired
    lateinit var commonRestTemplate: RestTemplate

    @Autowired
    lateinit var cardService: CardService

    @Autowired
    lateinit var userSyncStatusService: UserSyncStatusService

    @Autowired
    lateinit var cardTransactionService: CardTransactionService

    @MockBean
    lateinit var headerService: HeaderService

    val userId = 1L
    val organizationId = "shinhancard"

    @BeforeEach
    fun before() {
        given(headerService.makeHeader(userId.toString(), organizationId)).willReturn(
            mutableMapOf(
                "contentType" to MediaType.APPLICATION_JSON_VALUE,
                "authorization" to "Bearer 123",
                "clientId" to "596d66692c4069c168b57c59"
            ))
    }

    @Test
    @Rollback
    @Transactional
    fun transactionShadowingTest() {
        setupServerPaging()
        val executionContext = requestSetting()
        val now = DateTimeUtil.utcNowLocalDateTime()
        userSyncStatusService.upsertUserSyncStatus(
            executionContext.userId.toLong(),
            executionContext.organizationId,
            Transaction.cardTransaction.name,
            DateTimeUtil.utcLocalDateTimeToEpochMilliSecond(),
            true
        )

        val response = cardTransactionService.listTransactions(executionContext, now)

        val shadowingResponse = cardTransactionPublishService.shadowing(userId, organizationId, now, executionContext.executionRequestId, response)
        Assert.assertEquals(false, shadowingResponse.isDiff)

        val listSize = shadowingResponse.oldList.size
        val oldTransactions = shadowingResponse.oldList as List<CardTransaction>
        val transactions = shadowingResponse.dbList as List<CardTransaction>

        for (i in 0 until listSize) {
            val isDiffObject = listOf(
                oldTransactions[i].cardName == transactions[i].cardName,
                oldTransactions[i].cardNumber == transactions[i].cardNumber,
                oldTransactions[i].cardNumberMask == transactions[i].cardNumberMask,
                oldTransactions[i].cardCompanyCardId == transactions[i].cardCompanyCardId,
                oldTransactions[i].businessLicenseNumber == transactions[i].businessLicenseNumber,
                oldTransactions[i].storeName == transactions[i].storeName,
                oldTransactions[i].storeNumber == transactions[i].storeNumber,
                oldTransactions[i].cardType == transactions[i].cardType,
                oldTransactions[i].cardTypeOrigin == transactions[i].cardTypeOrigin,
                oldTransactions[i].cardTransactionType == transactions[i].cardTransactionType,
                oldTransactions[i].cardTransactionTypeOrigin == transactions[i].cardTransactionTypeOrigin,
                oldTransactions[i].currencyCode == transactions[i].currencyCode,
                oldTransactions[i].isInstallmentPayment == transactions[i].isInstallmentPayment,
                oldTransactions[i].installment == transactions[i].installment,
                oldTransactions[i].installmentRound == transactions[i].installmentRound,
                oldTransactions[i].netSalesAmount == transactions[i].netSalesAmount,
                oldTransactions[i].serviceChargeAmount == transactions[i].serviceChargeAmount,
                oldTransactions[i].tax == transactions[i].tax,
                oldTransactions[i].paidPoints == transactions[i].paidPoints,
                oldTransactions[i].isPointPay == transactions[i].isPointPay,
                oldTransactions[i].discountAmount == transactions[i].discountAmount,
                oldTransactions[i].amount == transactions[i].amount,
                oldTransactions[i].canceledAmount == transactions[i].canceledAmount,
                oldTransactions[i].partialCanceledAmount == transactions[i].partialCanceledAmount,
                oldTransactions[i].approvalNumber == transactions[i].approvalNumber,
                oldTransactions[i].approvalDay == transactions[i].approvalDay,
                oldTransactions[i].approvalTime == transactions[i].approvalTime,
                oldTransactions[i].pointsToEarn == transactions[i].pointsToEarn,
                oldTransactions[i].isOverseaUse == transactions[i].isOverseaUse,
                oldTransactions[i].paymentDay == transactions[i].paymentDay,
                oldTransactions[i].storeCategory == transactions[i].storeCategory,
                oldTransactions[i].transactionCountry == transactions[i].transactionCountry
            ).all { it }

            Assert.assertEquals(true, isDiffObject)
        }
    }

    private fun requestSetting(): CollectExecutionContext {
        val executionContext: CollectExecutionContext = CollectExecutionContext(
            executionRequestId = UUID.randomUUID().toString(),
            organizationId = organizationId,
            userId = userId.toString(),
            startAt = DateTimeUtil.utcNowLocalDateTime()
        )
        return executionContext
    }

    fun setupServerPaging() {
        val creditDomesticAPI = ShinhancardApis.card_shinhancard_credit_domestic_transactions
        val creditOverseaAPI = ShinhancardApis.card_shinhancard_credit_oversea_transactions
        val checkDomesticAPI = ShinhancardApis.card_shinhancard_check_domestic_transactions
        val checkOverseaAPI = ShinhancardApis.card_shinhancard_check_oversea_transactions
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        // 신용카드 국내
        ExecutionTestUtil.serverSetting(
            server,
            creditDomesticAPI,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_credit_domestic_p1.json"
        )
        ExecutionTestUtil.serverSetting(
            server,
            creditDomesticAPI,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_credit_domestic_p2.json"
        )
        // 신용카드 해외
        ExecutionTestUtil.serverSetting(
            server,
            creditOverseaAPI,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_credit_oversea_p1.json"
        )
        ExecutionTestUtil.serverSetting(
            server,
            creditOverseaAPI,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_credit_oversea_p2.json"
        )
        // 체크카드 국내
        ExecutionTestUtil.serverSetting(
            server,
            checkDomesticAPI,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_check_domestic_p1.json"
        )
        ExecutionTestUtil.serverSetting(
            server,
            checkDomesticAPI,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_check_domestic_p2.json"
        )
        // 체크카드 해외
        ExecutionTestUtil.serverSetting(
            server,
            checkOverseaAPI,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_check_oversea_p1.json"
        )
        ExecutionTestUtil.serverSetting(
            server,
            checkOverseaAPI,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_check_oversea_p2.json"
        )
    }
}
