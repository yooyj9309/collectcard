package com.rainist.collectcard.plcc.cardrewards

import com.github.tomakehurst.wiremock.WireMockServer
import com.rainist.collectcard.common.collect.api.TestLottecardPlccApis
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.plcc.cardrewards.RewardsApiMockSetting.Companion.makeCollectExecutionContext
import com.rainist.collectcard.plcc.cardrewards.RewardsApiMockSetting.Companion.makePlccRpcRequestOnFeb
import com.rainist.collectcard.plcc.cardrewards.testservice.TestPlccCardRewardsServiceImpl
import com.rainist.collectcard.plcc.common.db.entity.PlccCardRewardsEntity
import com.rainist.collectcard.plcc.common.db.entity.PlccCardRewardsSummaryEntity
import com.rainist.collectcard.plcc.common.db.repository.PlccCardRewardsRepository
import com.rainist.collectcard.plcc.common.db.repository.PlccCardRewardsSummaryRepository
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
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.transaction.Transactional

@DisplayName("PlccCardRewardsService 테스트")
@SpringBootTest
class PlccCardRewardsServiceTest {

    @Autowired
    lateinit var testPlccCardRewardsServiceImpl: TestPlccCardRewardsServiceImpl

    @Autowired
    lateinit var plccCardRewardsRepository: PlccCardRewardsRepository

    @Autowired
    lateinit var plccCardRewardsSummaryRepository: PlccCardRewardsSummaryRepository

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

    @Test
    @Transactional
    @Rollback
    @DisplayName("RewardsSummary 송수신 후 DB저장")
    fun rewardsSummary_db_save() {
        // when
        testPlccCardRewardsServiceImpl.getPlccRewards(
            executionContext = makeCollectExecutionContext(),
            rpcRequest = makePlccRpcRequestOnFeb()
        )

        // then
        assertThat(plccCardRewardsSummaryRepository.findAll().size).isEqualTo(1)
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("RewardsSummary update 테스트")
    fun rewardsSummary_update_test() {
        savedARewardsSummary()

        assertThat(plccCardRewardsSummaryRepository.findAll().size).isEqualTo(1)

        // when
        testPlccCardRewardsServiceImpl.getPlccRewards(
            executionContext = makeCollectExecutionContext(),
            rpcRequest = makePlccRpcRequestOnFeb()
        )

        val plccCardRewardsSummaryEntity = plccCardRewardsSummaryRepository.findAll()[0]

        // then
        // 기존값 - totalBenefitAmount(15000 -> 17000), totalSalesAmount(80000 -> 81500)
        assertAll(
            "rewardsSummary update",
            { assertThat(plccCardRewardsSummaryEntity.totalBenefitAmount).isEqualTo(BigDecimal("17000.0000")) },
            { assertThat(plccCardRewardsSummaryEntity.totalSalesAmount).isEqualTo(BigDecimal("81500.0000")) }
        )
    }

    private fun savedARewardsSummary() {
        plccCardRewardsSummaryRepository.save(PlccCardRewardsSummaryEntity().apply {
            banksaladUserId = 1
            cardCompanyId = "596d66692c4069c168b57c77"
            cardCompanyCardId = "376277600833685"
            benefitYearMonth = "202102"
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
            responseCode = "0000"
            responseMessage = "waS788OzuK61x776vcC0z7TZLg=="
        })
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("TypeLimit 송수신 후 DB 저장")
    fun typeLimit_test() {
        // when
        testPlccCardRewardsServiceImpl.getPlccRewards(
            executionContext = makeCollectExecutionContext(),
            rpcRequest = makePlccRpcRequestOnFeb()
        )

        val savedTypeLimitEntities = plccCardRewardsRepository.findAll()

        // then
        assertThat(savedTypeLimitEntities.size).isEqualTo(4)
        /** TypeLimit 리스트에서 마지막 데이터는 총계 데이터인데 DB에 저장하지 않는다.
         *  마지막 데이터가 총계데이터가 아니어야 하니까 last()를 꺼내 테스트 중
         */

        assertThat(savedTypeLimitEntities.last()).isEqualToComparingOnlyGivenFields(
            PlccCardRewardsEntity().apply {
                banksaladUserId = 1
                cardCompanyId = "596d66692c4069c168b57c77"
                cardCompanyCardId = "376277600833685"
                benefitYearMonth = "202102"
                benefitName = "빨대카드 편의점 할인"
                benefitCode = "C295"
            },
            "banksaladUserId",
            "cardCompanyId",
            "cardCompanyCardId",
            "benefitYearMonth",
            "benefitName",
            "benefitCode"
        )
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("TypeLimit update 테스트")
    fun typeLimit_update_test() {
        // given
        savedATypeLimit()

        assertThat(plccCardRewardsRepository.findAll().size).isEqualTo(1)

        // when
        testPlccCardRewardsServiceImpl.getPlccRewards(
            executionContext = makeCollectExecutionContext(),
            rpcRequest = makePlccRpcRequestOnFeb()
        )

        val savedTypeLimitEntities = plccCardRewardsRepository.findAll()
        assertThat(savedTypeLimitEntities.size).isEqualTo(4)

        // then : total_limit_amount(20000 -> 25000), apply_amount(3000 -> 5000)
        assertAll(
            "typeLimit update",
            { assertThat(savedTypeLimitEntities[0].totalLimitAmount).isEqualTo(BigDecimal("25000.0000")) },
            { assertThat(savedTypeLimitEntities[0].appliedAmount).isEqualTo(BigDecimal("5000.0000")) }
        )
    }

    private fun savedATypeLimit() {
        plccCardRewardsRepository.save(PlccCardRewardsEntity().apply {
            banksaladUserId = 1
            cardCompanyId = "596d66692c4069c168b57c77"
            cardCompanyCardId = "376277600833685"
            benefitYearMonth = "202102"
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
}
