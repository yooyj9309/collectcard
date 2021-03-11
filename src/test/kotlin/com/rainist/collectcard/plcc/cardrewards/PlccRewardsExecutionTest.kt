package com.rainist.collectcard.plcc.cardrewards

import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.common.collect.api.LottecardPlccApis
import com.rainist.collectcard.common.execution.MockExecutions
import com.rainist.collectcard.common.util.ExecutionTestUtil
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsRequest
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsRequestDataBody
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsResponse
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardThreshold
import com.rainist.collectcard.plcc.cardrewards.dto.PromotionCode
import java.math.BigDecimal
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestTemplate

@SpringBootTest
@DisplayName("실적한도 Execution 테스트")
class PlccRewardsExecutionTest {

    @Autowired
    lateinit var commonRestTemplate: RestTemplate

    @Autowired
    lateinit var collectExecutorService: CollectExecutorService

    @Test
    fun plccRewardsExecutionTest() {
        // given
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        ExecutionTestUtil.serverSetting(
            server,
            LottecardPlccApis.card_lottecard_plcc_rewards,
            "classpath:mock/lottecard/rewards/rewards_expected_1.json"
        )

        // when
        val executionResponse = ExecutionTestUtil.getExecutionResponse<PlccCardRewardsResponse>(
            collectExecutorService = collectExecutorService,
            execution = MockExecutions.lottecardPlccRewards,
            executionContext = ExecutionTestUtil.getExecutionContext("1", "lottecard"),
            executionRequest = makeRewardsRequest()
        )

        val plccThreshold = executionResponse.response?.dataBody?.plccCardThreshold
        val plccTypeLimits = executionResponse.response?.dataBody?.benefitList ?: mutableListOf()

        // then
        assertThat(plccTypeLimits.size).isEqualTo(5)
        assertThat(plccThreshold).isEqualToComparingFieldByField(PlccCardThreshold().apply {
            this.outcomeStartDate = "20210201"
            this.outcomeEndDate = "20210228"
            this.isOutcomeDelay = false
            this.beforeMonthCriteriaUseAmount = BigDecimal("000001550000")
            this.outcomeCriteriaAmount = BigDecimal("000000500000")
            this.totalBenefitAmount = BigDecimal("000000017000")
            this.totalBenefitCount = 4
            this.totalSalesAmount = BigDecimal("000000081500")
            this.monthlyBenefitRate = BigDecimal("00.00")
            this.monthlyBenefitLimit = BigDecimal("000000050000")
            this.cashbackAmount = BigDecimal("00000000")
            this.message = ""
            this.promotionCode = PromotionCode.ISSUED
            this.responseCode = "0000"
            this.responseMessage = "waS788OzuK61x776vcC0z7TZLg=="
            this.benefitListCount = 5
        })
    }

    private fun makeRewardsRequest(): ExecutionRequest<PlccCardRewardsRequest> {
        return ExecutionRequest.builder<PlccCardRewardsRequest>()
            .headers(mutableMapOf<String, String>())
            .request(PlccCardRewardsRequest().apply {
                dataBody = PlccCardRewardsRequestDataBody()
            })
            .build()
    }
}
