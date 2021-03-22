/*
package com.rainist.collectcard.plcc.cardrewards

import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.common.collect.api.LottecardPlccApis
import com.rainist.collectcard.common.enums.ResultCode
import com.rainist.collectcard.common.execution.MockExecutions
import com.rainist.collectcard.common.util.ExecutionTestUtil
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewards
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsRequest
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsRequestDataBody
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsResponse
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsSummary
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardThreshold
import com.rainist.collectcard.plcc.cardrewards.dto.PromotionCode
import com.rainist.collectcard.plcc.cardrewards.dto.ServiceType
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
    lateinit var lottePlccExecutorService: CollectExecutorService

    @Autowired
    lateinit var lottePlccRestTemplate: RestTemplate

    @DisplayName("Threshold execution 테스트")
    @Test
    fun plccRewardsExecutionTest() {
        // given
        val server = MockRestServiceServer.bindTo(lottePlccRestTemplate).ignoreExpectOrder(true).build()

        ExecutionTestUtil.serverSetting(
            server,
            LottecardPlccApis.card_lottecard_plcc_rewards,
            "classpath:mock/lottecard/rewards/rewards_threshold_expected_1.json"
        )

        // when
        val executionResponse = ExecutionTestUtil.getExecutionResponse<PlccCardRewardsResponse>(
            collectExecutorService = lottePlccExecutorService,
            execution = MockExecutions.lottecardPlccRewards,
            executionContext = ExecutionTestUtil.getExecutionContext("1", "lottecard"),
            executionRequest = makeRewardsRequest()
        )

        val plccThreshold = executionResponse.response?.dataBody?.plccCardThreshold
        val plccTypeLimitSummary = executionResponse.response?.dataBody?.plccCardRewardsSummary
        val plccTypeLimits = executionResponse.response?.dataBody?.plccCardRewardsList ?: mutableListOf()

        // then
        assertThat(plccTypeLimits.size).isEqualTo(5)
        assertThat(plccThreshold).isEqualToComparingFieldByField(PlccCardThreshold().apply {
            this.outcomeStartDate = "20210201"
            this.outcomeEndDate = "20210228"
            this.isOutcomeDelay = false
            this.beforeMonthCriteriaUseAmount = BigDecimal("000001550000")
            this.outcomeCriteriaAmount = BigDecimal("000000500000")
            this.responseCode = ResultCode.OK.name
            this.responseMessage = "waS788OzuK61x776vcC0z7TZLg=="
        })
        assertThat(plccTypeLimitSummary).isEqualToComparingFieldByField(PlccCardRewardsSummary().apply {
            this.totalBenefitAmount = BigDecimal("000000017000")
            this.totalBenefitCount = 4
            this.totalSalesAmount = BigDecimal("000000081500")
            this.monthlyBenefitRate = BigDecimal("00.00")
            this.monthlyBenefitLimit = BigDecimal("000000050000")
            this.cashbackAmount = BigDecimal("00000000")
            this.message = ""
            this.promotionCode = PromotionCode.ISSUED
            this.responseCode = ResultCode.OK.name
            this.responseMessage = "waS788OzuK61x776vcC0z7TZLg=="
            this.benefitListCount = 5
        })
        assertThat(plccTypeLimits[0]).isEqualToComparingFieldByField(PlccCardRewards().apply {
            this.benefitName = "u6G068SrteUgxKvG5CDH0sDO"
            this.benefitCode = "C292"
            this.discountRate = BigDecimal("000005000.00")
            this.totalLimitAmount = BigDecimal("000000025000")
            this.appliedAmount = BigDecimal("000000005000")
            this.limitRemainingAmount = BigDecimal("000000020000")
            this.totalLimitCount = 0
            this.appliedCount = 1
            this.limitRemainingCount = 0
            this.totalSalesLimitAmount = BigDecimal("0")
            this.appliedSalesAmount = BigDecimal("000000020500")
            this.limitRemainingSalesAmount = BigDecimal("0")
            this.serviceType = ServiceType.CHARGE_DISCOUNT
        })
    }

    @DisplayName("이전 달 실적이 없어 한도, 잔여금액이 99999로 패딩되어 오는 경우에 0으로 매핑 테스트")
    @Test
    fun tyepLimit_padding_test() {
        // given
        val server = MockRestServiceServer.bindTo(lottePlccRestTemplate).ignoreExpectOrder(true).build()

        ExecutionTestUtil.serverSetting(
            server,
            LottecardPlccApis.card_lottecard_plcc_rewards,
            "classpath:mock/lottecard/rewards/rewards_padding_expected_1.json"
        )

        // when
        val executionResponse = ExecutionTestUtil.getExecutionResponse<PlccCardRewardsResponse>(
            collectExecutorService = lottePlccExecutorService,
            execution = MockExecutions.lottecardPlccRewards,
            executionContext = ExecutionTestUtil.getExecutionContext("1", "lottecard"),
            executionRequest = makeRewardsRequest()
        )

        val plccTypeLimits = executionResponse.response?.dataBody?.plccCardRewardsList ?: mutableListOf()

        // then
        assertThat(plccTypeLimits.size).isEqualTo(1)
        assertThat(plccTypeLimits[0]).isEqualToComparingFieldByField(PlccCardRewards().apply {
            this.benefitName = "u6G068SrteUgxKvG5CDH0sDO"
            this.benefitCode = "C292"
            this.discountRate = BigDecimal("000005000.00")
            this.totalLimitAmount = BigDecimal("0")
            this.appliedAmount = BigDecimal("000000005000")
            this.limitRemainingAmount = BigDecimal("0")
            this.totalLimitCount = 0
            this.appliedCount = 1
            this.limitRemainingCount = 0
            this.totalSalesLimitAmount = BigDecimal("0")
            this.appliedSalesAmount = BigDecimal("000000020500")
            this.limitRemainingSalesAmount = BigDecimal("0")
            this.serviceType = ServiceType.CHARGE_DISCOUNT
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


 */
