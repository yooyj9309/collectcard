/*
package com.rainist.collectcard.plcc.cardrewards

import com.github.banksalad.idl.apis.v1.plcc.PlccProto
import com.rainist.collectcard.common.collect.api.LottecardPlccApis
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.util.ExecutionTestUtil
import com.rainist.collectcard.plcc.cardrewards.dto.PlccRpcRequest
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
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
class PlccTypeLimitPublishImplTest {

    @Autowired
    lateinit var plccCardRewardsPublishService: PlccCardRewardsPublishService

    @Autowired
    lateinit var plccCardRewardsService: PlccCardRewardsService

    @Autowired
    lateinit var lottePlccRestTemplate: RestTemplate

    @MockBean
    lateinit var headerService: HeaderService

    lateinit var collectExecutionContext: CollectExecutionContext

    lateinit var plccRpcRequest: PlccRpcRequest

    @DisplayName("Rewards 데이터 저장")
    @BeforeEach
    fun setUp() {
        val server = MockRestServiceServer.bindTo(lottePlccRestTemplate).ignoreExpectOrder(true).build()

        ExecutionTestUtil.serverSetting(
            server,
            LottecardPlccApis.card_lottecard_plcc_rewards,
            "classpath:mock/lottecard/rewards/rewards_typeLimit_expected_1.json"
        )

        collectExecutionContext = CollectExecutionContext(
            executionRequestId = UUID.randomUUID().toString(),
            organizationId = "596d66692c4069c168b57c77",
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
            requestMonthMs = 1613026702000
        )

        plccCardRewardsService.getPlccRewards(
            executionContext = collectExecutionContext,
            rpcRequest = plccRpcRequest
        )
    }

    @DisplayName("Rewards Publish Test")
    @Transactional
    @Rollback
    @Test
    fun rewards_publish_success() {
        // when
        val rewardsProto = plccCardRewardsPublishService.rewardsPublish(
            executionContext = collectExecutionContext,
            request = plccRpcRequest
        )

        // then
        val rewardsList = rewardsProto.getRewardsTypeLimit(0)
        assertAll(
            "rewards proto test",
            { assertThat(rewardsProto.appliedRewardsAmount2F).isEqualTo(1700000L) },
            { assertThat(rewardsProto.totalSalesAmount2F.value).isEqualTo(8150000) },
            { assertThat(rewardsList.rewardsTypeName).isEqualTo(PlccProto.RewardsType.REWARDS_TYPE_CAFE_DISCOUNT) },
            { assertThat(rewardsList.rewardsCode.value).isEqualTo("C292") },
            { assertThat(rewardsList.rewardsLimitAmount2F).isEqualTo(2500000L) }
        )
    }
}


 */
