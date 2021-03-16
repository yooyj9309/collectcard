/*
package com.rainist.collectcard.plcc.cardrewards

import com.rainist.collectcard.common.collect.api.LottecardPlccApis
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.util.ExecutionTestUtil
import com.rainist.collectcard.plcc.cardrewards.dto.PlccRpcRequest
import com.rainist.collectcard.plcc.common.db.entity.PlccCardThresholdEntity
import com.rainist.collectcard.plcc.common.db.entity.PlccCardTypeLimitEntity
import com.rainist.collectcard.plcc.common.db.repository.PlccCardThresholdRepository
import com.rainist.collectcard.plcc.common.db.repository.PlccCardTypeLimitRepository
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

@DisplayName("PlccRewardsService 테스트")
@SpringBootTest
class PlccRewardsServiceTest {

    @Autowired
    lateinit var plccCardThresholdService: PlccCardThresholdService

    @Autowired
    lateinit var plccCardThresholdRepository: PlccCardThresholdRepository

    @Autowired
    lateinit var plccCardTypeLimitService: PlccCardTypeLimitService

    @Autowired
    lateinit var plccCardTypeLimitRepository: PlccCardTypeLimitRepository

    @MockBean
    lateinit var headerService: HeaderService

    companion object : Log

    @DisplayName("Threshold 송수신 후 저장 테스트")
    @Transactional
    @Rollback
    @Test
    fun getThreshold_success() {
        // given
        mockServerSetting("classpath:mock/lottecard/rewards/rewards_threshold_expected_1.json")

        // when
        plccCardThresholdService.getPlccCardThreshold(
            executionContext = makeCollectExecutionContext(),
            rpcRequest = makePlccRpcRequestOnMar()
        )

        val savedThresholdEntities = plccCardThresholdRepository.findAll()

        assertThat(savedThresholdEntities.size).isEqualTo(1)
    }

    @DisplayName("Threshold update 테스트")
    @Transactional
    @Rollback
    @Test
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
            beforeMonthCriteriaUseAmount = BigDecimal("1550000")
            outcomeCriteriaAmount = BigDecimal("500000")
            totalBenefitAmount = BigDecimal("15000")
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
            responseCode = "0000"
            responseMessage = "waS788OzuK61x776vcC0z7TZLg=="
        })
        assertThat(plccCardThresholdRepository.findAll().size).isEqualTo(1)

        // when
        mockServerSetting("classpath:mock/lottecard/rewards/rewards_threshold_expected_1.json")

        // when
        plccCardThresholdService.getPlccCardThreshold(
            executionContext = makeCollectExecutionContext(),
            rpcRequest = makePlccRpcRequestOnMar()
        )

        // then
        val plccCardThresholdEntity = plccCardThresholdRepository.findAll()[0]

        // 기존값 - totalBenefitAmount(15000 -> 17000), totalSalesAmount(80000 -> 81500)
        assertAll(
            "threshold update",
            { assertThat(plccCardThresholdEntity.totalBenefitAmount).isEqualTo(BigDecimal(17000)) },
            { assertThat(plccCardThresholdEntity.totalSalesAmount).isEqualTo(BigDecimal(81500)) }
        )
    }

    @DisplayName("TypeLimit 송수신 후 DB 저장")
    @Transactional
    @Rollback
    @Test
    fun typeLimit_test() {
        // given
        mockServerSetting("classpath:mock/lottecard/rewards/rewards_typeLimit_expected_1.json")
        savedAThreshold()

        assertThat(plccCardThresholdRepository.findAll().size).isEqualTo(1)

        // when
        plccCardTypeLimitService.listPlccCardTypeLimit(
            executionContext = makeCollectExecutionContext(),
            rpcRequest = makePlccRpcRequestOnFeb()
        )

        val savedTypeLimitEntities = plccCardTypeLimitRepository.findAll()

        // then
        assertThat(savedTypeLimitEntities.size).isEqualTo(4)
        */
/** TypeLimit 리스트에서 마지막 데이터는 총계 데이터인데 DB에 저장하지 않는다.
         *  마지막 데이터가 총계데이터가 아니어야 하니까 last()를 꺼내 테스트 중
         *//*

        assertThat(savedTypeLimitEntities.last()).isEqualToComparingOnlyGivenFields(
            PlccCardTypeLimitEntity().apply {
                banksaladUserId = 1
                cardCompanyId = "596d66692c4069c168b57c77"
                cardCompanyCardId = "376277600833685"
                benefitYearMonth = "202102"
                outcomeStartDay = "20210201"
                outcomeEndDay = "20210228"
                benefitName = "빨대카드 편의점 할인"
                benefitCode = "C295"
            },
            "banksaladUserId",
            "cardCompanyId",
            "cardCompanyCardId",
            "benefitYearMonth",
            "outcomeStartDay",
            "outcomeEndDay",
            "benefitName",
            "benefitCode"
        )
    }

    @DisplayName("TypeLimit update 테스트")
    @Transactional
    @Rollback
    @Test
    fun typeLimit_update_test() {
        // given
        savedATypeLimit()

        assertThat(plccCardTypeLimitRepository.findAll().size).isEqualTo(1)

        mockServerSetting("classpath:mock/lottecard/rewards/rewards_typeLimit_expected_1.json")

        // when
        plccCardTypeLimitService.listPlccCardTypeLimit(
            executionContext = makeCollectExecutionContext(),
            rpcRequest = makePlccRpcRequestOnFeb()
        )

        val savedTypeLimitEntities = plccCardTypeLimitRepository.findAll()
        assertThat(savedTypeLimitEntities.size).isEqualTo(4)

        // then : total_limit_amount(20000 -> 25000), apply_amount(3000 -> 5000)
        assertAll(
            "typeLimit update",
            { assertThat(savedTypeLimitEntities[0].totalLimitAmount).isEqualTo(BigDecimal(25000)) },
            { assertThat(savedTypeLimitEntities[0].appliedAmount).isEqualTo(BigDecimal(5000)) }
        )
    }

    private fun savedATypeLimit() {
        plccCardTypeLimitRepository.save(PlccCardTypeLimitEntity().apply {
            banksaladUserId = 1
            cardCompanyId = "596d66692c4069c168b57c77"
            cardCompanyCardId = "376277600833685"
            benefitYearMonth = "202102"
            outcomeStartDay = "20210201"
            outcomeEndDay = "20210228"
            benefitName = "u6G068SrteUgxKvG5CDH0sDO"
            benefitCode = "C292"
            discountRate = BigDecimal("000005000.00")
            totalLimitAmount = BigDecimal("000000020000")
            appliedAmount = BigDecimal("000000003000")
            limitRemainingAmount = BigDecimal("000000020000")
            totalLimitCount = 999
            appliedCount = 1
            limitRemainingCount = 999
            totalSalesLimitAmount = BigDecimal("999999999999")
            appliedSaleAmount = BigDecimal("000000020500")
            limitRemainingSalesAmount = BigDecimal("999999999999")
            serviceType = "CHARGE_DISCOUNT"
            lastCheckAt = LocalDateTime.now()
            createdAt = LocalDateTime.now()
            updatedAt = LocalDateTime.now()
        })
    }

    private fun makePlccRpcRequestOnMar(): PlccRpcRequest {
        return PlccRpcRequest(
            cardId = "376277600833685",
            requestMonthMs = 1615427107000 // 2021년 3월 11일 목요일 오전 10:45:07 GMT+09:00
        )
    }

    private fun makePlccRpcRequestOnFeb(): PlccRpcRequest {
        return PlccRpcRequest(
            cardId = "376277600833685",
            requestMonthMs = 1613026702000 // 2021년 2월 11일 목요일 오후 3:58:22 GMT+09:00
        )
    }

    private fun makeCollectExecutionContext() = CollectExecutionContext(
        executionRequestId = UUID.randomUUID().toString(),
        userId = "1",
        organizationId = "596d66692c4069c168b57c77"
    )

    private fun savedAThreshold() {
        plccCardThresholdRepository.save(PlccCardThresholdEntity().apply {
            banksaladUserId = 1
            cardCompanyId = "596d66692c4069c168b57c77"
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
    }

    private fun mockServerSetting(filePath: String) {
        val server = MockRestServiceServer.bindTo(lottePlccRestTemplate).ignoreExpectOrder(true).build()

        ExecutionTestUtil.serverSetting(
            server,
            LottecardPlccApis.card_lottecard_plcc_rewards,
            filePath
        )

        given(headerService.makeHeader(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(
                mutableMapOf(
                    CONTENT_TYPE to MediaType.APPLICATION_JSON_VALUE
                )
            )
    }
}
*/
