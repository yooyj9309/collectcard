package com.rainist.collectcard.plcc.cardrewards

import com.github.banksalad.idl.apis.v1.collectcard.CollectcardProto
import com.github.banksalad.idl.apis.v1.plcc.PlccProto
import com.github.tomakehurst.wiremock.WireMockServer
import com.google.protobuf.Int64Value
import com.google.protobuf.StringValue
import com.rainist.collectcard.common.collect.api.TestLottecardPlccApis
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.plcc.cardrewards.RewardsApiMockSetting.Companion.makeCollectExecutionContext
import com.rainist.collectcard.plcc.cardrewards.RewardsApiMockSetting.Companion.makePlccRpcRequestOnFeb
import com.rainist.collectcard.plcc.cardrewards.testservice.TestPlccCardRewardsServiceImpl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cloud.contract.wiremock.WireMockSpring
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class PlccRewarsPublishImplTest {

    @Autowired
    lateinit var plccCardRewardsPublishService: PlccCardRewardsPublishService

    @Autowired
    lateinit var testPlccCardRewardsServiceImpl: TestPlccCardRewardsServiceImpl

    @MockBean
    lateinit var headerService: HeaderService

    companion object {
        lateinit var wireMockServer: WireMockServer

        @BeforeAll
        @JvmStatic
        fun setUp() {
            wireMockServer = WireMockServer(WireMockSpring.options().dynamicPort())
            wireMockServer.start()
            TestLottecardPlccApis.init(wireMockServer.port().toString())
            RewardsApiMockSetting.setupMockServer(wireMockServer, RewardsApiMockSetting.RewardsApis.REWARDS)
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            wireMockServer.shutdown()
        }
    }

    @DisplayName("Rewards 데이터 저장")
    @BeforeEach
    fun dbSave() {
        testPlccCardRewardsServiceImpl.getPlccRewards(
            makeCollectExecutionContext(),
            makePlccRpcRequestOnFeb()
        )
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Rewards Publish Test")
    fun rewards_publish_success() {
        // when
        val getPlccRewardsResponse = plccCardRewardsPublishService.rewardsPublish(
            makeCollectExecutionContext(),
            makePlccRpcRequestOnFeb()
        )

        // then
        assertThat(getPlccRewardsResponse).isEqualToIgnoringGivenFields(
            CollectcardProto.GetPlccRewardsResponse.newBuilder()
                .setAppliedRewardsAmount2F(1700000L)
                .setAppliedRewardsCount(Int64Value.of(4))
                .setTotalSalesAmount2F(Int64Value.of(8150000L))
                .setEarnedRewardsRate2F(Int64Value.of(0))
                .setRewardsLimitAmount2F(5000000L)
                .setCashbackAmount2F(Int64Value.of(0))
                .setRewardsDetailMessage(StringValue.of(""))
                .setPromotionType(PlccProto.RewardsPromotionType.REWARDS_PROMOTION_TYPE_ISSUED)
                .build(),
            "rewardsTypeLimit_"
        )

        val rewardsTypeLimitBuilder = CollectcardProto.RewardsTypeLimit.newBuilder()

        rewardsTypeLimitBuilder.rewardsTypeName = PlccProto.RewardsType.REWARDS_TYPE_CAFE_DISCOUNT
        rewardsTypeLimitBuilder.rewardsCode = StringValue.of("C292")
        rewardsTypeLimitBuilder.rewardsLimitAmount2F = 2500000L
        rewardsTypeLimitBuilder.rewardsLimitUsedAmount2F = 500000L
        rewardsTypeLimitBuilder.rewardsLimitRemainingAmount2F = 2000000L

        rewardsTypeLimitBuilder.rewardsLimitCount = Int64Value.of(0)
        rewardsTypeLimitBuilder.rewardsLimitUsedCount = Int64Value.of(1)
        rewardsTypeLimitBuilder.rewardsLimitRemainingCount =
            Int64Value.of(0)

        rewardsTypeLimitBuilder.rewardsLimitSalesAmount2F =
            Int64Value.of(0)
        rewardsTypeLimitBuilder.rewardsLimitUsedSalesAmount2F =
            Int64Value.of(2050000)
        rewardsTypeLimitBuilder.rewardsLimitRemainingSalesAmount2F =
            Int64Value.of(0)
        rewardsTypeLimitBuilder.serviceType = PlccProto.RewardsServiceType.REWARDS_SERVICE_TYPE_CHARGE_DISCOUNT

        val rewardsTypeLimit = rewardsTypeLimitBuilder.build()

        assertThat(getPlccRewardsResponse.rewardsTypeLimitList.first()).isEqualToComparingFieldByField(
            rewardsTypeLimit
        )
    }
}
