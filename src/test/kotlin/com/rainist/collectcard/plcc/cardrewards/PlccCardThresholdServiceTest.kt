package com.rainist.collectcard.plcc.cardrewards

import com.github.tomakehurst.wiremock.WireMockServer
import com.rainist.collectcard.common.collect.api.TestLottecardPlccApis
import com.rainist.collectcard.common.enums.ResultCode
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.plcc.cardrewards.RewardsApiMockSetting.Companion.makeCollectExecutionContext
import com.rainist.collectcard.plcc.cardrewards.RewardsApiMockSetting.Companion.makePlccRpcRequestOnMar
import com.rainist.collectcard.plcc.cardrewards.testservice.TestPlccCardThresholdServiceImpl
import com.rainist.collectcard.plcc.common.db.entity.PlccCardThresholdEntity
import com.rainist.collectcard.plcc.common.db.repository.PlccCardThresholdRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cloud.contract.wiremock.WireMockSpring
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@DisplayName("PlccCardThresholdService 테스트")
@SpringBootTest
class PlccCardThresholdServiceTest {

    @Autowired
    lateinit var testPlccCardThresholdServiceImpl: TestPlccCardThresholdServiceImpl

    @Autowired
    lateinit var plccCardThresholdRepository: PlccCardThresholdRepository

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
            RewardsApiMockSetting.setupMockServer(wireMockServer, RewardsApiMockSetting.RewardsApis.THRESHOLD)
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            wireMockServer.shutdown()
        }
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Threshold 송수신 후 저장 테스트")
    fun getThreshold_success() {
        // when
        testPlccCardThresholdServiceImpl.getPlccCardThreshold(
            executionContext = makeCollectExecutionContext(),
            rpcRequest = makePlccRpcRequestOnMar()
        )

        val savedThresholdEntities = plccCardThresholdRepository.findAll()

        assertThat(savedThresholdEntities.size).isEqualTo(1)
        assertThat(savedThresholdEntities.first()).isEqualToComparingOnlyGivenFields(
            PlccCardThresholdEntity().apply {
                responseCode = ResultCode.OK.name
                responseMessage = "정상처리되었습니다."
            },
            "responseCode", "responseMessage"
        )
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Threshold update 테스트")
    fun threshold_update_test() {
        // given
        plccCardThresholdRepository.save(PlccCardThresholdEntity().apply {
            banksaladUserId = 1
            cardCompanyId = "596d66692c4069c168b57c77"
            cardCompanyCardId = "376277600833685"
            benefitYearMonth = "202102"
            outcomeStartDay = "20210201"
            outcomeEndDay = "20210228"
            isOutcomeDelay = false
            beforeMonthCriteriaUseAmount = BigDecimal("1500000")
            outcomeCriteriaAmount = BigDecimal("400000")
            lastCheckAt = LocalDateTime.now()
            createdAt = LocalDateTime.now()
            updatedAt = LocalDateTime.now()
            responseCode = "0000"
            responseMessage = "waS788OzuK61x776vcC0z7TZLg=="
        })
        assertThat(plccCardThresholdRepository.findAll().size).isEqualTo(1)

        // when
        testPlccCardThresholdServiceImpl.getPlccCardThreshold(
            executionContext = makeCollectExecutionContext(),
            rpcRequest = makePlccRpcRequestOnMar()
        )

        // then
        val plccCardThresholdEntity = plccCardThresholdRepository.findAll()[0]

        // 기존값 - beforeMonthCriteriaUseAmount(1500000 -> 1550000), outcomeCriteriaAmount(400000 -> 500000)
        assertAll(
            "threshold update",
            { assertThat(plccCardThresholdEntity.beforeMonthCriteriaUseAmount).isEqualTo(BigDecimal("1550000.0000")) },
            { assertThat(plccCardThresholdEntity.outcomeCriteriaAmount).isEqualTo(BigDecimal("500000.0000")) }
        )
    }
}
