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
    lateinit var plccCardTypeLimitPublishService: PlccCardRewardsPublishService

    @Autowired
    lateinit var plccCardTypeLimitService: PlccCardTypeLimitService

    @MockBean
    lateinit var headerService: HeaderService

    lateinit var collectExecutionContext: CollectExecutionContext

    lateinit var plccRpcRequest: PlccRpcRequest

    @DisplayName("TypeLimit 데이터 저장")
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

        plccCardTypeLimitService.listPlccCardTypeLimit(
            executionContext = collectExecutionContext,
            rpcRequest = plccRpcRequest
        )
    }

    @DisplayName("TypleLimit Publish Test")
    @Transactional
    @Rollback
    @Test
    fun typeLimit_publish_success() {
        // when
        val rewardsTypeLimitPublish = plccCardTypeLimitPublishService.rewardsTypeLimitPublish(
            executionContext = collectExecutionContext,
            request = plccRpcRequest
        )

        // then
        val rewardsTypeLimit = rewardsTypeLimitPublish.rewardsTypeLimitList[0]
        assertAll(
            "typeLimit proto test",
            { assertThat(rewardsTypeLimit.rewardsTypeName).isEqualTo(PlccProto.RewardsType.REWARDS_TYPE_CAFE_DISCOUNT) },
            { assertThat(rewardsTypeLimit.rewardsCode.value).isEqualTo("C292") },
            { assertThat(rewardsTypeLimit.rewardsLimitAmount2F).isEqualTo(2500000L) }
        )
    }
}
*/
