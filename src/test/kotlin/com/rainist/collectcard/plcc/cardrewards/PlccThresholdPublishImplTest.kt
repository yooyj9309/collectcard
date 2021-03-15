package com.rainist.collectcard.plcc.cardrewards

import com.rainist.collectcard.common.collect.api.LottecardPlccApis
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.util.ExecutionTestUtil
import com.rainist.collectcard.plcc.cardrewards.dto.PlccRpcRequest
import java.util.UUID
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate

@SpringBootTest
class PlccThresholdPublishImplTest {

    @Autowired
    lateinit var commonRestTemplate: RestTemplate

    @Autowired
    lateinit var plccCardThresholdService: PlccCardThresholdService

    @Autowired
    lateinit var plccRewardsPublishService: PlccCardRewardsPublishService

    @MockBean
    lateinit var headerService: HeaderService

    lateinit var collectExecutionContext: CollectExecutionContext

    lateinit var plccRpcRequest: PlccRpcRequest

    @DisplayName("DB 저장")
    @BeforeEach
    fun setUp() {
        // given
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        ExecutionTestUtil.serverSetting(
            server,
            LottecardPlccApis.card_lottecard_plcc_rewards,
            "classpath:mock/lottecard/rewards/rewards_threshold_expected_1.json"
        )

        collectExecutionContext = CollectExecutionContext(
            executionRequestId = UUID.randomUUID().toString(),
            organizationId = "",
            userId = "4339"
        )

        BDDMockito.given(headerService.makeHeader(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(
                mutableMapOf(
                    HttpHeaders.CONTENT_TYPE to MediaType.APPLICATION_JSON_VALUE
                )
            )

        plccRpcRequest = PlccRpcRequest(
            cardId = "376277600833685",
            requestMonthMs = 1615427107000
        )

        plccCardThresholdService.getPlccCardThreshold(
            executionContext = collectExecutionContext,
            rpcRequest = plccRpcRequest
        )
    }

    @DisplayName("Threshold Publish Test")
    @Rollback
    @Transactional
    @Test
    fun threshold_publish_success() {
        // when
        val rewardsThresholdPublish = plccRewardsPublishService.rewardsThresholdPublish(
            executionContext = collectExecutionContext,
            request = plccRpcRequest
        )

        // then
        Assertions.assertThat(rewardsThresholdPublish.rewardsThreshold.isRewardsThresholdSuspended.value).isFalse()
        Assertions.assertThat(rewardsThresholdPublish.rewardsThreshold.promotionType.name)
            .isEqualTo("REWARDS_PROMOTION_TYPE_ISSUED")
    }
}
