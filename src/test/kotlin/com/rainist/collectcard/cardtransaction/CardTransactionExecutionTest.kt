package com.rainist.collectcard.cardtransaction

import com.rainist.collect.common.execution.Execution
import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.cardtransactions.dto.CardTransaction
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsRequest
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsRequestDataBody
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsRequestDataHeader
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponseDataBody
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.enums.CardTransactionType
import com.rainist.collectcard.common.enums.CardType
import com.rainist.collectcard.common.execution.MockExecutions
import com.rainist.collectcard.common.util.ExecutionTestUtil
import java.math.BigDecimal
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestTemplate

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@DisplayName("Transaction Execution 테스트")
class CardTransactionExecutionTest {

    @Autowired
    lateinit var commonRestTemplate: RestTemplate

    @Autowired
    lateinit var collectExecutorService: CollectExecutorService

    /**
     * shinhancard
     * 1. 신용 국내사용내역조회-일시불/할부 SHC_HPG00428
     * 2. 신용 해외사용내역조회-일시불조회 SHC_HPG01612
     * 3. 체크 국내사용내역 조회 SHC_HPG01030
     * 4. 체크 해외사용내역 조회 SHC_HPG01031
     */

    // 1. 신용 국내사용내역조회-일시불/할부 SHC_HPG00428
    @Test
    fun shinhancardCreditDomesticTransactionTest() {
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()
        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_credit_domestic_transactions,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_credit_domestic_p1.json"
        )

        val dataBody = getListTransactionsResponseDataBody(MockExecutions.shinhancardCreditDomesticTransactions)

        assertEquals(dataBody?.transactions?.size, 3)
        assertEquals("1", dataBody.nextKey)
        assertThat(dataBody?.transactions?.get(0)).isEqualToComparingFieldByField(CardTransaction().apply {
            this.cardNumber = "687"
            this.cardNumberMask = "687"
            this.cardCompanyCardId = ""
            this.storeName = "지에스(GS)25 구로동양"
            this.storeNumber = "0086807609"
            this.cardType = CardType.CREDIT
            this.cardTransactionType = CardTransactionType.PURCHASE
            this.cardTransactionTypeOrigin = "1"
            this.currencyCode = "KRW"
            this.isInstallmentPayment = true
            this.installment = 3
            this.amount = BigDecimal("2000")
            this.canceledAmount = BigDecimal("0")
            this.partialCanceledAmount = BigDecimal("0")
            this.approvalNumber = "26591309"
            this.approvalDay = "20200709"
            this.approvalTime = "131320"
            this.paymentDay = "20200725"
            this.isOverseaUse = false
        })
    }

    // 2. 신용 해외사용내역조회-일시불조회 SHC_HPG01612
    @Test
    fun shinhancardCreditOverseaTransactionTest() {
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()
        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_credit_oversea_transactions,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_credit_oversea_p1.json"
        )

        val dataBody = getListTransactionsResponseDataBody(MockExecutions.shinhancardCreditOverseaTransactions)

        assertEquals(dataBody?.transactions?.size, 1)
        assertEquals("1", dataBody.nextKey)
        assertThat(dataBody?.transactions?.get(0)).isEqualToComparingFieldByField(CardTransaction().apply {
            this.cardNumber = "4518-****-****-2435"
            this.cardNumberMask = "4518-****-****-2435"
            this.cardCompanyCardId = ""
            this.storeName = "FACEBK *4US5YLNYT2"
            this.cardType = CardType.CREDIT
            this.cardTransactionType = CardTransactionType.SINGLE_PAYMENT
            this.cardTransactionTypeOrigin = "1"
            this.currencyCode = "USD"
            this.amount = BigDecimal("35.0")
            this.installment = 0
            this.canceledAmount = BigDecimal("0")
            this.approvalNumber = "283905"
            this.approvalDay = "20190614"
            this.approvalTime = "192400"
            this.isOverseaUse = true
            this.paymentDay = "20190715"
            this.storeCategory = "광고"
            this.transactionCountry = "UNITED KINGDOM"
        })
    }

    // 3. 체크 국내사용내역 조회 SHC_HPG01030
    @Test
    fun shinhancardCheckDomesticTransaction() {
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()
        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_check_domestic_transactions,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_check_domestic_p1.json"
        )

        val dataBody = getListTransactionsResponseDataBody(MockExecutions.shinhancardCheckDomesticTransactions)

        assertEquals(dataBody?.transactions?.size, 1)
        assertEquals("1", dataBody.nextKey)
        assertThat(dataBody?.transactions?.get(0)).isEqualToComparingFieldByField(CardTransaction().apply {
            this.cardNumber = "654"
            this.cardNumberMask = "654"
            this.cardCompanyCardId = ""
            this.storeName = "본까즈"
            this.storeNumber = "0046036042"
            this.cardType = CardType.DEBIT
            this.cardTransactionType = CardTransactionType.PURCHASE
            this.cardTransactionTypeOrigin = "1"
            this.currencyCode = "KRW"
            this.amount = BigDecimal("6000")
            this.installment = 0
            this.canceledAmount = BigDecimal("0")
            this.approvalNumber = "43462070"
            this.approvalDay = "20190923"
            this.approvalTime = "122825"
            this.paymentDay = "20191010"
            this.isOverseaUse = false
        })
    }

    // 4. 체크 해외사용내역 조회 SHC_HPG01031
    @Test
    fun shinhancardCheckOverseaTransaction() {
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()
        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_check_oversea_transactions,
            "classpath:mock/shinhancard/transaction/card_transaction_expected_1_check_oversea_p1.json"
        )

        val dataBody = getListTransactionsResponseDataBody(MockExecutions.shinhancardCheckOverseaTransactions)

        assertEquals(dataBody?.transactions?.size, 1)
        assertEquals("1", dataBody.nextKey)
        assertThat(dataBody?.transactions?.get(0)).isEqualToComparingFieldByField(CardTransaction().apply {
            this.cardNumber = "236"
            this.cardNumberMask = "236"
            this.cardCompanyCardId = ""
            this.storeName = "GOOGLE *YouTubePremium"
            this.cardType = CardType.DEBIT
            this.cardTransactionType = CardTransactionType.APPROVAL
            this.currencyCode = "인도루피"
            this.amount = BigDecimal("129.00")
            this.installment = 0
            this.canceledAmount = BigDecimal("0")
            this.approvalNumber = "455685"
            this.approvalDay = "20200104"
            this.approvalTime = "004801"
            this.isOverseaUse = true
            this.paymentDay = "20200210"
            this.storeCategory = "유선TV"
            this.transactionCountry = "미국"
        })
    }

    private fun getListTransactionsResponseDataBody(execution: Execution): ListTransactionsResponseDataBody {
        val res: ExecutionResponse<ListTransactionsResponse> = ExecutionTestUtil.getExecutionResponse(
            collectExecutorService,
            execution,
            ExecutionTestUtil.getExecutionContext("1", "shinhancard"),
            makeTransactionRequest()
        )
        return res.response.dataBody ?: ListTransactionsResponseDataBody()
    }

    fun makeTransactionRequest(): ExecutionRequest<ListTransactionsRequest> {
        return ExecutionRequest.builder<ListTransactionsRequest>()
            .headers(mutableMapOf<String, String?>())
            .request(ListTransactionsRequest().apply {
                this.dataHeader = ListTransactionsRequestDataHeader()
                this.dataBody = ListTransactionsRequestDataBody()
            }).build()
    }
}
