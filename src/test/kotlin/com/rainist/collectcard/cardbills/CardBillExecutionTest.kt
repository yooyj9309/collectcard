package com.rainist.collectcard.cardbills

import com.rainist.collect.common.api.Api
import com.rainist.collect.common.execution.Execution
import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.cardbills.dto.BillCardType
import com.rainist.collectcard.cardbills.dto.CardBill
import com.rainist.collectcard.cardbills.dto.CardBillTransaction
import com.rainist.collectcard.cardbills.dto.ListBillTransactionsResponse
import com.rainist.collectcard.cardbills.dto.ListCardBillsRequest
import com.rainist.collectcard.cardbills.dto.ListCardBillsRequestDataBody
import com.rainist.collectcard.cardbills.dto.ListCardBillsResponse
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.collect.execution.shinhancard.ShinhancardBillExecution
import com.rainist.collectcard.common.collect.execution.shinhancard.ShinhancardBillTransactionExpectedExecution
import com.rainist.collectcard.common.enums.CardType
import com.rainist.collectcard.common.execution.MockExecutions
import com.rainist.collectcard.common.util.ExecutionTestUtil
import java.math.BigDecimal
import java.time.LocalDateTime
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
@DisplayName("billExecution 테스트")
class CardBillExecutionTest {

    @Autowired
    lateinit var commonRestTemplate: RestTemplate

    @Autowired
    lateinit var collectExecutorService: CollectExecutorService

    /**
     * 1. 체크카드 월별 청구내역(SHC_HPG01226)
     * 2. 신용카드 월별 청구내역(SHC_HPG00719)
     * 3. 카드(체크) 월별청구내역조회(상세총괄) (SHC_HPG00537)
     * 4. 카드(신용) 월별청구내역조회(상세총괄) (SHC_HPG00698)
     * 5. 카드결제예정금액총괄 SHC_HPG01096_EXT
     * 6. 카드결제예정금액(일시불,현금서비스 상세) SHC_HPG00237
     * 7. (할부) 결제예정금액(할부, 론 상세) (SHC_HPG00238)
     * 8. bills(청구서) Execution 전체 테스트
     * 9. bill_transaction(결제예정금액) Execution 전체 테스트
     */

    // 1. 체크카드 월별 청구내역(SHC_HPG01226)
    @Test
    fun shinhancardCheckBillTest() {
        val res: ExecutionResponse<ListCardBillsResponse> = getExecutionWithServerSetting(
            ShinhancardApis.card_shinhancard_check_bills,
            MockExecutions.shinhancardCheckBills,
            "classpath:mock/shinhancard/bill/bill_check_expected_1.json"
        )

        val bills = res.response.dataBody?.cardBills ?: mutableListOf()

        assertEquals(bills.size, 2)
        assertThat(bills[0]).isEqualToComparingFieldByField(CardBill().apply {
            this.billNumber = "202008140001"
            this.billType = "0001"
            this.cardType = BillCardType.DEBIT
            this.userName = ""
            this.userGrade = ""
            this.paymentDay = "20200814"
            this.billedYearMonth = "202008"
            this.nextPaymentDay = ""
            this.paymentBankId = "088"
            this.paymentAccountNumber = "11031******0"
        })
    }

    // 2. 신용카드 월별 청구내역(SHC_HPG00719)
    @Test
    fun shinhancardCreditBillTest() {
        val res: ExecutionResponse<ListCardBillsResponse> = getExecutionWithServerSetting(
            ShinhancardApis.card_shinhancard_credit_bills,
            MockExecutions.shinhancardCreditBills,
            "classpath:mock/shinhancard/bill/bill_credit_expected_1.json"
        )

        val bills = res.response.dataBody?.cardBills ?: mutableListOf()
        assertEquals(bills.size, 2)
        assertThat(bills[0]).isEqualToComparingFieldByField(CardBill().apply {
            this.billNumber = "202008140002"
            this.billType = "0002"
            this.cardType = BillCardType.CREDIT
            this.userName = ""
            this.userGrade = ""
            this.paymentDay = "20200814"
            this.billedYearMonth = "202008"
            this.nextPaymentDay = ""
            this.billingAmount = BigDecimal("633780")
            this.paymentBankId = "088"
            this.paymentAccountNumber = "11031******0"
        })
    }

    // 3. 카드_[EXT] (체크) 월별청구내역조회(상세총괄) (SHC_HPG00537)
    @Test
    fun shinhancardCheckBillTransactionTest() {
        val res: ExecutionResponse<ListBillTransactionsResponse> = getExecutionWithServerSetting(
            ShinhancardApis.card_shinhancard_check_bill_transactions,
            MockExecutions.shinhancardcheckBillTransaction,
            "classpath:mock/shinhancard/bill/bill_check_detail_expected_1.json"
        )

        val transactions = res.response?.dataBody?.billTransactions ?: mutableListOf()
        assertEquals(transactions.size, 5)
        assertThat(transactions[0]).isEqualToComparingFieldByField(CardBillTransaction().apply {
            this.cardNumber = "28"
            this.cardNumberMasked = "28"
            this.storeName = "(주)티몬"
            this.cardType = CardType.DEBIT
            this.discountAmount = BigDecimal("0")
            this.amount = BigDecimal("74500")
            this.approvalDay = "20200702"
            this.paidAmount = BigDecimal("74500")
            this.billedAmount = BigDecimal("0")
            this.billedFee = BigDecimal("0")
        })
    }

    // 4. 카드_[EXT] (신용) 월별청구내역조회(상세총괄) (SHC_HPG00698)
    @Test
    fun shinhancardCreditBillTransactionTest() {
        val res: ExecutionResponse<ListBillTransactionsResponse> = getExecutionWithServerSetting(
            ShinhancardApis.card_shinhancard_credit_bill_transactions,
            MockExecutions.shinhancardCreditBillTransaction,
            "classpath:mock/shinhancard/bill/bill_credit_detail_expected_1.json"
        )

        val transactions = res.response?.dataBody?.billTransactions ?: mutableListOf()
        assertEquals(transactions.size, 5)
        assertThat(transactions[0]).isEqualToComparingFieldByField(CardBillTransaction().apply {
            this.cardNumber = "753"
            this.cardNumberMasked = "753"
            this.storeName = "06월지하철1건"
            this.isInstallmentPayment = false
            this.amount = BigDecimal("1250")
            this.approvalDay = "20200630"
            this.isOverseaUse = false
            this.billedAmount = BigDecimal("1250")
            this.billedFee = BigDecimal("0")
        })
    }

    // 5. 카드_[EXT] 결제예정금액총괄 SHC_HPG01096_EXT
    @Test
    fun shinhancardBillsExpected() {
        val res: ExecutionResponse<ListCardBillsResponse> = getExecutionWithServerSetting(
            ShinhancardApis.card_shinhancard_list_user_card_bills_expected,
            MockExecutions.shinhancardBillsExpected,
            "classpath:mock/shinhancard/bill/bill_transactions_expected_1.json"
        )

        val bills = res.response?.dataBody?.cardBills ?: mutableListOf()
        assertEquals(bills.size, 1)
        assertThat(bills[0]).isEqualToComparingFieldByField(CardBill().apply {
            this.billNumber = "202009140002"
            this.billType = "0002"
            this.cardType = BillCardType.UNKNOWN
            this.userName = ""
            this.userGrade = ""
            this.paymentDay = "20200914"
            this.billedYearMonth = ""
            this.nextPaymentDay = ""
            this.billingAmount = BigDecimal("281731")
            this.paymentBankId = "088"
            this.paymentAccountNumber = "11031******0"
        })
    }

    // 6. 카드_[EXT] 결제예정금액(일시불,현금서비스 상세) SHC_HPG00237
    @Test
    fun shinhancardBillsExpectedDetailLumpSum() {
        val res: ExecutionResponse<ListBillTransactionsResponse> = getExecutionWithServerSetting(
            ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_lump_sum,
            MockExecutions.shinhancardBillsExpectedDetailLumpSum,
            "classpath:mock/shinhancard/bill/bill_transactions_expected_detail_lump_sum_p1.json"
        )

        val transactions = res.response?.dataBody?.billTransactions ?: mutableListOf()
        assertEquals(5, transactions.size)
        assertThat(transactions[0]).isEqualToComparingFieldByField(CardBillTransaction().apply {
            this.cardName = "아시아나 Air 1.5"
            this.cardNumber = "5155********8753"
            this.cardNumberMasked = "5155-****-****-8753" // 서비스코드에서 - 제거
            this.storeName = "구글페이먼트코리아"
            this.isInstallmentPayment = false
            this.amount = BigDecimal("8690")
            this.approvalDay = "20200813"
            this.billedAmount = BigDecimal("8690")
            this.billedFee = BigDecimal("0")
            this.isPaidFull = false
            this.pointsRate = BigDecimal("0")
        })
    }

    // 7. (할부) 결제예정금액(할부, 론 상세) (SHC_HPG00238)
    @Test
    fun shinhancardBillsExpectedDetailInstallment() {
        val res: ExecutionResponse<ListBillTransactionsResponse> = getExecutionWithServerSetting(
            ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_installment,
            MockExecutions.shinhancardBillsExpectedDetailInstallment,
            "classpath:mock/shinhancard/bill/bill_transactions_expected_detail_installment_p1.json"
        )

        val transactions = res.response?.dataBody?.billTransactions ?: mutableListOf()
        assertEquals(5, transactions.size)
        assertThat(transactions[0]).isEqualToComparingFieldByField(CardBillTransaction().apply {
            this.cardNumber = "4221********4437"
            this.cardNumberMasked = "4221-****-****-4437"
            this.storeName = "롯데홈)폴스미스유니코 .."
            this.isInstallmentPayment = true
            this.installment = 12
            this.installmentRound = 11
            this.amount = BigDecimal("82613")
            this.approvalDay = "20191120"
            this.billingRound = 11
            this.billedAmount = BigDecimal("6800")
            this.billedFee = BigDecimal("201")
            this.remainingAmount = BigDecimal("6800")
            this.pointsRate = BigDecimal("90")
        })
    }

    // 8. billExecution 통합 테스트
    @Test
    fun cardShinhancardBillsTest() {
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()
        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_check_bills, "classpath:mock/shinhancard/bill/bill_check_expected_1.json")
        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_check_bill_transactions, "classpath:mock/shinhancard/bill/bill_check_detail_expected_1.json")
        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_credit_bills, "classpath:mock/shinhancard/bill/bill_credit_expected_1.json")
        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_credit_bill_transactions, "classpath:mock/shinhancard/bill/bill_credit_detail_expected_2.json")

        val res: ExecutionResponse<ListCardBillsResponse> = ExecutionTestUtil.getExecutionResponse(
            collectExecutorService,
            ShinhancardBillExecution.cardShinhancardBills,
            ExecutionTestUtil.getExecutionContext("1", "shinhancard", LocalDateTime.of(2020, 7, 30, 0, 0)),
            makeBillRequest()
        )

        val bills = res.response.dataBody?.cardBills ?: mutableListOf()

        assertEquals(2, bills.size)
        assertEquals(5, bills[0].transactions?.size)
        assertEquals(1, bills[1].transactions?.size)

        // 체크카드 청구금액은 0원으로 계산
        assertEquals(BigDecimal("0"), bills[0].billingAmount)

        assertThat(bills[1]).isEqualToIgnoringGivenFields(CardBill().apply {
            this.billNumber = "202008140002"
            this.billType = "0002"
            this.cardType = BillCardType.CREDIT
            this.userName = ""
            this.userGrade = ""
            this.paymentDay = "20200814"
            this.billedYearMonth = "202008"
            this.nextPaymentDay = ""
            this.billingAmount = BigDecimal("633780")
            this.paymentBankId = "088"
            this.paymentAccountNumber = "11031******0"
        }, "transactions")

        assertThat(bills[1].transactions?.get(0)).isEqualToComparingFieldByField(CardBillTransaction().apply {
            this.cardNumber = "753"
            this.cardNumberMasked = "753"
            this.storeName = "05월지하철3건"
            this.isInstallmentPayment = false
            this.amount = BigDecimal("1850")
            this.approvalDay = "20200531"
            this.isOverseaUse = false
            this.billedAmount = BigDecimal("1850")
            this.billedFee = BigDecimal("0")
            this.billNumber = "202008140002"
            this.billType = "0002"
        })
    }

    // 9 bill_transaction(결제예정금액) Execution 전체 테스트
    @Test
    fun cardShinhancardBillTransactionExpectedTest() {
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()
        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_list_user_card_bills_expected, "classpath:mock/shinhancard/bill/bill_transactions_expected_1.json")
        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_lump_sum, "classpath:mock/shinhancard/bill/bill_transactions_expected_detail_lump_sum_p1.json")
        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_lump_sum, "classpath:mock/shinhancard/bill/bill_transactions_expected_detail_lump_sum_p2.json")

        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_list_user_card_bills_expected, "classpath:mock/shinhancard/bill/bill_transactions_expected_1.json")
        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_installment, "classpath:mock/shinhancard/bill/bill_transactions_expected_detail_installment_p1.json")
        ExecutionTestUtil.serverSetting(server, ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_installment, "classpath:mock/shinhancard/bill/bill_transactions_expected_detail_installment_p2.json")

        val res: ExecutionResponse<ListCardBillsResponse> = ExecutionTestUtil.getExecutionResponse(
            collectExecutorService,
            ShinhancardBillTransactionExpectedExecution.cardShinhancardBillTransactionExpected,
            ExecutionTestUtil.getExecutionContext("1", "shinhancard", LocalDateTime.of(2020, 7, 30, 0, 0)),
            makeBillRequest()
        )

        val bills = res.response.dataBody?.cardBills ?: mutableListOf()
        assertEquals(1, bills.size)
        assertEquals(12, bills[0].transactions?.size)

        assertThat(bills[0]).isEqualToIgnoringGivenFields(CardBill().apply {
            this.billNumber = "202009140002"
            this.billType = "0002"
            this.cardType = BillCardType.UNKNOWN
            this.userName = ""
            this.userGrade = ""
            this.paymentDay = "20200914"
            this.billedYearMonth = ""
            this.nextPaymentDay = ""
            this.billingAmount = BigDecimal("281731")
            this.paymentBankId = "088"
            this.paymentAccountNumber = "11031******0"
        }, "transactions")

        assertThat(bills[0]?.transactions?.get(0)).isEqualToComparingFieldByField(CardBillTransaction().apply {
            this.cardName = "아시아나 Air 1.5"
            this.cardNumber = "5155********8753"
            this.cardNumberMasked = "5155-****-****-8753" // 서비스코드에서 - 제거
            this.storeName = "구글페이먼트코리아"
            this.isInstallmentPayment = false
            this.amount = BigDecimal("8690")
            this.approvalDay = "20200813"
            this.billedAmount = BigDecimal("8690")
            this.billedFee = BigDecimal("0")
            this.isPaidFull = false
            this.pointsRate = BigDecimal("0")
            this.billNumber = "202009140002"
            this.billType = "0002"
        })
    }

    private fun <T> getExecutionWithServerSetting(api: Api, execution: Execution, mockDataPath: String): ExecutionResponse<T> {
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()
        ExecutionTestUtil.serverSetting(server, api, mockDataPath)
        return ExecutionTestUtil.getExecutionResponse(
            collectExecutorService,
            execution,
            ExecutionTestUtil.getExecutionContext("1", "shinhancard"),
            makeBillRequest()
        )
    }

    fun makeBillRequest(): ExecutionRequest<ListCardBillsRequest> {
        return ExecutionRequest.builder<ListCardBillsRequest>()
            .headers(mutableMapOf<String, String?>())
            .request(ListCardBillsRequest().apply {
                dataBody = ListCardBillsRequestDataBody()
            })
            .build()
    }
}
