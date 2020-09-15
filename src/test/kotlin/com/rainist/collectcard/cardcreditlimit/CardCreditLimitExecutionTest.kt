package com.rainist.collectcard.cardcreditlimit

import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.cardcreditlimit.dto.CardCreditLimitRequestDataBody
import com.rainist.collectcard.cardcreditlimit.dto.CardCreditLimitRequestDataHeader
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimit
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitRequest
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitResponse
import com.rainist.collectcard.cardcreditlimit.dto.Limit
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.execution.MockExecutions
import com.rainist.collectcard.common.util.ExecutionTestUtil
import java.math.BigDecimal
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
@DisplayName("CreditLimit Execution 테스트")
class CardCreditLimitExecutionTest {

    @Autowired
    lateinit var commonRestTemplate: RestTemplate

    @Autowired
    lateinit var collectExecutorService: CollectExecutorService

    /**
     * shinhancard
     * 1. 신용 한도조회 (SHC_HPG01730)
     */

    @Test
    fun shinhancardCreditLimitTest() {
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_credit_limit,
            "classpath:mock/shinhancard/creditlimit/card_credit_limit_expected_1.json"
        )

        val res: ExecutionResponse<CreditLimitResponse> = ExecutionTestUtil.getExecutionResponse(
            collectExecutorService,
            MockExecutions.shinhancardCreditLimit,
            ExecutionTestUtil.getExecutionContext("1", "shinhancard"),
            makeCreditLimitRequest()
        )

        val creditLimit = res.response?.dataBody?.creditLimitInfo ?: CreditLimit()

        assertThat(creditLimit).isEqualToComparingFieldByField(CreditLimit().apply {
            this.loanLimit = Limit().apply {
                this.totalLimitAmount = BigDecimal("10000000")
                this.remainedAmount = BigDecimal("9966500")
                this.usedAmount = BigDecimal("33500")
            }
            this.onetimePaymentLimit = Limit().apply {
                this.totalLimitAmount = BigDecimal("10000000")
                this.remainedAmount = BigDecimal("9966500")
                this.usedAmount = BigDecimal("33500")
            }
            this.cardLoanLimit = Limit().apply {
                this.totalLimitAmount = BigDecimal("3000000")
                this.remainedAmount = BigDecimal("3000000")
                this.usedAmount = BigDecimal("0")
            }
            this.creditCardLimit = null
            this.debitCardLimit = null
            this.cashServiceLimit = Limit().apply {
                this.totalLimitAmount = BigDecimal("3000000")
                this.remainedAmount = BigDecimal("3000000")
                this.usedAmount = BigDecimal("0")
            }
            this.overseaLimit = null
            this.installmentLimit = Limit().apply {
                this.totalLimitAmount = BigDecimal("10000000")
                this.remainedAmount = BigDecimal("9966500")
                this.usedAmount = BigDecimal("0")
            }
        })
    }

    fun makeCreditLimitRequest(): ExecutionRequest<CreditLimitRequest> {
        return ExecutionRequest.builder<CreditLimitRequest>()
            .headers(mutableMapOf<String, String?>())
            .request(CreditLimitRequest().apply {
                this.dataHeader = CardCreditLimitRequestDataHeader()
                this.dataBody = CardCreditLimitRequestDataBody()
            }).build()
    }
}
