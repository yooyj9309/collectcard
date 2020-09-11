package com.rainist.collectcard.cardloan

import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.cardloans.dto.ListLoansRequest
import com.rainist.collectcard.cardloans.dto.ListLoansRequestDataBody
import com.rainist.collectcard.cardloans.dto.ListLoansRequestDataHeader
import com.rainist.collectcard.cardloans.dto.ListLoansResponse
import com.rainist.collectcard.cardloans.dto.Loan
import com.rainist.collectcard.cardloans.dto.LoanResponseDataHeader
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.collect.execution.shinhancard.ShinhancardLoanExecution
import com.rainist.collectcard.common.enums.ResultCode
import com.rainist.collectcard.common.execution.MockExecutions
import com.rainist.collectcard.common.util.ExecutionTestUtil
import java.math.BigDecimal
import junit.framework.Assert.assertEquals
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
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
@DisplayName("대출Execution 테스트")
class CardLoanExecutionTest<CreditLimitResponse> {

    @Autowired
    lateinit var commonRestTemplate: RestTemplate

    @Autowired
    lateinit var collectExecutorService: CollectExecutorService

    @Test
    fun shinhancardLoanInfoTest() {
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_loan_info,
            "classpath:mock/shinhancard/loan/card_loan_expected_1.json"
        )

        val res: ExecutionResponse<ListLoansResponse> = ExecutionTestUtil.getExecutionResponse(
            collectExecutorService,
            MockExecutions.shinhancardloanInfo,
            ExecutionTestUtil.getExecutionContext("1", "shinhancard"),
            makeLoanRequest()
        )

        val loans = res.response.dataBody?.loans ?: mutableListOf()
        assertEquals(loans.size, 2)
        assertThat(loans[0]).isEqualToComparingFieldByField(Loan().apply {
            this.loanId = "0005"
            this.loanNumber = "0005"
            this.loanName = "스피드론플러스"
        })

        assertThat(loans[1]).isEqualToComparingFieldByField(Loan().apply {
            this.loanId = "0004"
            this.loanNumber = "0004"
            this.loanName = "스피드론이지"
        })
    }

    @Test
    fun shinhancardLoanDetailTest() {
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_loan_detail,
            "classpath:mock/shinhancard/loan/card_loan_expected_1_detail_1.json"
        )

        val res: ExecutionResponse<Loan> = ExecutionTestUtil.getExecutionResponse(
            collectExecutorService,
            MockExecutions.shinhancardloanDetail,
            ExecutionTestUtil.getExecutionContext("1", "shinhancard"),
            makeLoanRequest()
        )

        val responseLoan: Loan = res.response ?: Loan()
        assertThat(responseLoan).isEqualToComparingFieldByField(Loan().apply {
            this.loanId = "0005"
            this.loanNumber = "0005"
            this.loanAmount = BigDecimal("3000000")
            this.remainingAmount = BigDecimal("3000000")
            this.issuedDay = "20200225"
            this.expirationDay = "20200728"
            this.repaymentMethodOrigin = "05"
            this.interestRate = BigDecimal("14.9")
            this.dataHeader = LoanResponseDataHeader().apply {
                this.resultCode = ResultCode.OK
                this.resultMessage = "성공적으로 조회 되었습니다."
            }
        })
        // assertThat(loanDetail).isEqualToIgnoringNullFields(loan)
        // assertThat(loanDetail).isEqualToIgnoringGivenFields(loan,"dataHeader")
    }

    /**
     *  최종적으로 Loan에 Detail 조회 + name의 결과 리턴
     */
    @Test
    fun shinhancardTotalTest() {
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_loan_info,
            "classpath:mock/shinhancard/loan/card_loan_expected_1.json"
        )

        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_loan_detail,
            "classpath:mock/shinhancard/loan/card_loan_expected_1_detail_1.json",
            "classpath:mock/shinhancard/loan/card_loan_expected_1_detail_2.json"
        )

        val res: ExecutionResponse<ListLoansResponse> = ExecutionTestUtil.getExecutionResponse(
            collectExecutorService,
            ShinhancardLoanExecution.cardShinhancardLoan,
            ExecutionTestUtil.getExecutionContext("1", "shinhancard"),
            makeLoanRequest()
        )

        val loans = res.response.dataBody?.loans ?: mutableListOf()
        assertEquals(loans.size, 2)
        assertThat(loans[0]).isEqualToComparingFieldByField(Loan().apply {
            this.loanId = "0005"
            this.loanNumber = "0005"
            this.loanName = "스피드론플러스"
            this.loanAmount = BigDecimal("3000000")
            this.remainingAmount = BigDecimal("3000000")
            this.issuedDay = "20200225"
            this.expirationDay = "20200728"
            this.repaymentMethodOrigin = "05" // 290 PR 머지 후, 주석해제
            this.interestRate = BigDecimal("14.9")
        })

        assertThat(loans[1]).isEqualToComparingFieldByField(Loan().apply {
            this.loanId = "0004"
            this.loanNumber = "0004"
            this.loanName = "스피드론이지"
            this.loanAmount = BigDecimal("5000000")
            this.remainingAmount = BigDecimal("5000000")
            this.issuedDay = "20200224"
            this.expirationDay = "20200728"
            this.repaymentMethodOrigin = "05" // 290 PR 머지 후, 주석해제
            this.interestRate = BigDecimal("14.8")
        })
    }

    fun makeLoanRequest(): ExecutionRequest<ListLoansRequest> {
        return ExecutionRequest.builder<ListLoansRequest>()
            .headers(mutableMapOf<String, String?>())
            .request(ListLoansRequest().apply {
                this.dataHeader = ListLoansRequestDataHeader()
                this.dataBody = ListLoansRequestDataBody()
            })
            .build()
    }
}
