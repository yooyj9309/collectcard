package com.rainist.collectcard.plcc.cardrewards

import com.rainist.collectcard.common.collect.api.LottecardPlccApis
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.util.ExecutionTestUtil
import com.rainist.collectcard.plcc.cardrewards.dto.PlccRpcRequest
import com.rainist.collectcard.plcc.common.db.entity.PlccCardThresholdEntity
import com.rainist.collectcard.plcc.common.db.repository.PlccCardThresholdRepository
import com.rainist.common.log.Log
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate

@SpringBootTest
class PlccRewardsServiceImplTest {

    @Autowired
    lateinit var plccCardThresholdService: PlccCardThresholdService

    @Autowired
    lateinit var plccRewardsPublishService: PlccCardRewardsPublishService

    @Autowired
    lateinit var plccCardThresholdRepository: PlccCardThresholdRepository

    @Autowired
    lateinit var commonRestTemplate: RestTemplate

    @MockBean
    lateinit var headerService: HeaderService

    companion object : Log

    @DisplayName("Threshold 송순신 후 저장 테스트")
    @Rollback
    @Transactional
    @Test
    fun getThreshold_success() {
        // given
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        ExecutionTestUtil.serverSetting(
            server,
            LottecardPlccApis.card_lottecard_plcc_rewards,
            "classpath:mock/lottecard/rewards/rewards_expected_1.json"
        )

        val collectExecutionContext = CollectExecutionContext(
            executionRequestId = UUID.randomUUID().toString(),
            organizationId = "",
            userId = "1"
        )

        given(headerService.makeHeader(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(
                mutableMapOf(
                    CONTENT_TYPE to MediaType.APPLICATION_JSON_VALUE
                )
            )

        val plccRpcRequest = PlccRpcRequest(
            cardId = "376277600833685",
            requestMonthMs = 1615427107000
        )

        // when
        plccCardThresholdService.getPlccCardThreshold(
            executionContext = collectExecutionContext,
            rpcRequest = plccRpcRequest
        )

        val savedThresholdEntities = plccCardThresholdRepository.findAll()

        assertThat(savedThresholdEntities.size).isEqualTo(1)
    }

    @DisplayName("Threshold update 테스트")
    @Rollback
    @Transactional
    @Test
    fun threshold_update_test() {
        // given
        plccCardThresholdRepository.save(PlccCardThresholdEntity().apply {
            banksaladUserId = 1
            cardCompanyId = "lottecard"
            cardCompanyCardId = "376277600833685"
            benefitYearMonth = "202102"
            outcomeStartDay = "20210201"
            outcomeEndDay = "20210228"
            isOutcomeDelay = false
            beforeMonthCriteriaUseAmount = BigDecimal("1550000")
            outcomeCriteriaAmount = BigDecimal("500000")
            totalBenefitAmount = BigDecimal("15000") // 기존 15000원
            totalBenefitCount = 4
            totalSalesAmount = BigDecimal("80000")
            monthlyBenefitRate = BigDecimal("00.00")
            monthlyBenefitLimit = BigDecimal("50000")
            cashbackAmount = BigDecimal("0")
            benefitMessage = ""
            promotionCode = "ISSUED"
            lastCheckAt = LocalDateTime.now()
            createdAt = LocalDateTime.now()
            updatedAt = LocalDateTime.now()
            // response_code = "0000"
            // response_message = "waS788OzuK61x776vcC0z7TZLg=="
        })
        assertThat(plccCardThresholdRepository.findAll().size).isEqualTo(1)

        // when : 기존값 - totalBenefitAmount(15000원), totalSalesAmount(80000원)
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        ExecutionTestUtil.serverSetting(
            server,
            LottecardPlccApis.card_lottecard_plcc_rewards,
            "classpath:mock/lottecard/rewards/rewards_expected_1.json"
        )

        val collectExecutionContext = CollectExecutionContext(
            executionRequestId = UUID.randomUUID().toString(),
            organizationId = "lottecard",
            userId = "1"
        )

        given(headerService.makeHeader(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(
                mutableMapOf(
                    CONTENT_TYPE to MediaType.APPLICATION_JSON_VALUE
                )
            )

        val plccRpcRequest = PlccRpcRequest(
            cardId = "376277600833685",
            requestMonthMs = 1615427107000
        )

        // when
        plccCardThresholdService.getPlccCardThreshold(
            executionContext = collectExecutionContext,
            rpcRequest = plccRpcRequest
        )

        // then
        val plccCardThresholdEntity = plccCardThresholdRepository.findAll()[0]

        assertAll(
            "threshold update",
            { assertThat(plccCardThresholdEntity.totalBenefitAmount).isEqualTo(BigDecimal(17000)) },
            { assertThat(plccCardThresholdEntity.totalSalesAmount).isEqualTo(BigDecimal(81500)) }
        )
    }
}
