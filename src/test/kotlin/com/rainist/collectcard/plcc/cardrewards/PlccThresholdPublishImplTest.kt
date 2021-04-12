package com.rainist.collectcard.plcc.cardrewards

import com.github.banksalad.idl.apis.v1.collectcard.CollectcardProto
import com.github.tomakehurst.wiremock.WireMockServer
import com.google.protobuf.BoolValue
import com.google.protobuf.Int64Value
import com.rainist.collectcard.common.collect.api.TestLottecardPlccApis
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.plcc.cardrewards.RewardsApiMockSetting.Companion.makeCollectExecutionContext
import com.rainist.collectcard.plcc.cardrewards.RewardsApiMockSetting.Companion.makePlccRpcRequestOnMar
import com.rainist.collectcard.plcc.cardrewards.testservice.TestPlccCardThresholdServiceImpl
import com.rainist.common.util.DateTimeUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cloud.contract.wiremock.WireMockSpring
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
class PlccThresholdPublishImplTest {

    @Autowired
    lateinit var testPlccCardThresholdServiceImpl: TestPlccCardThresholdServiceImpl

    @Autowired
    lateinit var plccRewardsPublishService: PlccCardRewardsPublishService

    @MockBean
    lateinit var headerService: HeaderService

    lateinit var collectExecutionContext: CollectExecutionContext

    companion object {
        lateinit var wireMockServer: WireMockServer

        @BeforeAll
        @JvmStatic
        fun setUp() {
            wireMockServer = WireMockServer(WireMockSpring.options().dynamicPort())
            wireMockServer.start()
            TestLottecardPlccApis.init(wireMockServer.port().toString())
            RewardsApiMockSetting.setupMockServer(
                wireMockServer,
                RewardsApiMockSetting.RewardsApis.THRESHOLD
            )
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            wireMockServer.shutdown()
        }
    }

    @BeforeEach
    fun dbSave() {
        testPlccCardThresholdServiceImpl.getPlccCardThreshold(
            makeCollectExecutionContext(),
            makePlccRpcRequestOnMar()
        )
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Threshold Publish Test")
    fun threshold_publish_success() {
        // when
        val rewardsThresholdPublish = plccRewardsPublishService.rewardsThresholdPublish(
            makeCollectExecutionContext(),
            makePlccRpcRequestOnMar()
        )
        val rewardsThreshold = rewardsThresholdPublish.rewardsThreshold

        // then
        /** 현재 로직 상 publish할 때 시,분,초를 현재 시각으로 한다.
         *  아래와 같이 시, 분, 초를 0, 0, 0으로 셋팅하고 isGreaterThan() 사용
         */
        assertAll(
            "Threshold start & end date",
            {
                assertThat(rewardsThreshold.rewardsStartsAtMs).isGreaterThan(
                    DateTimeUtil.kstLocalDateTimeToEpochMilliSecond(
                        LocalDateTime.of(2021, 2, 1, 0, 0, 0)
                    )
                )
            },
            {
                assertThat(rewardsThreshold.rewardsEndsAtMs).isGreaterThan(
                    DateTimeUtil.kstLocalDateTimeToEpochMilliSecond(
                        LocalDateTime.of(2021, 2, 28, 0, 0, 0)
                    )
                )
            })

        assertThat(rewardsThreshold).isEqualToComparingOnlyGivenFields(
            CollectcardProto.RewardsThreshold
                .newBuilder()
                .setIsRewardsThresholdSuspended(BoolValue.of(false))
                .setUsedAmountBasedOnRewardsThreshold2F(155000000L)
                .setMinimumAmountForRewardsThreshold2F(Int64Value.of(50000000))
                .build(),
            "isRewardsThresholdSuspended",
            "usedAmountBasedOnRewardsThreshold2F",
            "minimumAmountForRewardsThreshold2F"
        )
    }
}

