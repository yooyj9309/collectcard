package com.rainist.collectcard.plcc.cardrewards.testservice

import com.rainist.collect.common.execution.Execution
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.common.collect.api.TestLottecardPlccApis
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.enums.ResultCode
import com.rainist.collectcard.common.service.EncodeService
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.service.LocalDatetimeService
import com.rainist.collectcard.common.util.ExecutionTestUtil
import com.rainist.collectcard.plcc.cardrewards.PlccCardRewardsConvertService
import com.rainist.collectcard.plcc.cardrewards.PlccCardRewardsServiceImpl
import com.rainist.collectcard.plcc.cardrewards.RewardsApiMockSetting
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewards
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsRequest
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsRequestDataBody
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsResponse
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsSummary
import com.rainist.collectcard.plcc.cardrewards.dto.PlccRpcRequest
import com.rainist.collectcard.plcc.cardtransactions.convertStringYearMonth
import com.rainist.collectcard.plcc.common.db.repository.PlccCardRewardsHistoryRepository
import com.rainist.collectcard.plcc.common.db.repository.PlccCardRewardsRepository
import com.rainist.collectcard.plcc.common.db.repository.PlccCardRewardsSummaryRepository
import com.rainist.collectcard.plcc.common.util.PlccCardRewardsUtil
import com.rainist.common.log.Log
import com.rainist.common.service.ValidationService
import com.rainist.common.util.DateTimeUtil
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.nio.charset.Charset
import java.time.LocalDateTime

/** API 요청하는 부분을 mocking한 테스트용 클래스
 */
@Service
class TestPlccCardRewardsServiceImpl(
    val localDatetimeService: LocalDatetimeService,
    val headerService: HeaderService,
    val lottePlccExecutorService: CollectExecutorService,
    val validateService: ValidationService,
    val encodeService: EncodeService,
    val plccCardRewardsRepository: PlccCardRewardsRepository,
    val plccCardRewardsHistoryRepository: PlccCardRewardsHistoryRepository,
    val plccCardRewardsSummaryRepository: PlccCardRewardsSummaryRepository,
    val plccCardRewardsConvertService: PlccCardRewardsConvertService
) {

    companion object : Log

    fun getPlccRewards(
        executionContext: CollectExecutionContext,
        rpcRequest: PlccRpcRequest
    ) {

        val now = localDatetimeService.generateNowLocalDatetime().now

        val executionResponse = ExecutionTestUtil.getExecutionResponse<PlccCardRewardsResponse>(
            collectExecutorService = lottePlccExecutorService,
            execution = Execution.create()
                .exchange(TestLottecardPlccApis.card_lottecard_plcc_rewards)
                .to(PlccCardRewardsResponse::class.java)
                .build(),
            executionContext = ExecutionTestUtil.getExecutionContext("1", "lottecard"),
            executionRequest = RewardsApiMockSetting.makeRewardsRequest("202102")
        )

        val plccCardRewardsRequest = PlccCardRewardsRequest().apply {
            val requestYearMonth =
                DateTimeUtil.epochMilliSecondToKSTLocalDateTime(rpcRequest.requestMonthMs)
            val stringYearMonth = convertStringYearMonth(requestYearMonth)

            this.dataBody = PlccCardRewardsRequestDataBody().apply {
                this.inquiryYearMonth = stringYearMonth.yearMonth
                this.cardNumber = rpcRequest.cardId
            }
        }

        decodeKoreanFields(executionResponse)

        val plccCardRewardsList =
            (executionResponse.response?.dataBody?.plccCardRewardsList?.mapNotNull { plccCardRewards ->
                validateService.validateOrNull(plccCardRewards)
            }?.toMutableList()
                ?: mutableListOf())

        /* 혜택 종합(Rewards summary)과 혜택(Rewards) save */
        val plccCardTypeLimitSummary = executionResponse.response?.dataBody?.plccCardRewardsSummary
        if (plccCardTypeLimitSummary?.responseCode.equals(ResultCode.OK.name)) {

            // 혜택 종합 save
            plccCardRewardsConvertService.setScaleRewardsSummary(plccCardTypeLimitSummary)
            upsertRewardsTypeLimitSummary(
                executionContext = executionContext,
                rpcRequest = rpcRequest,
                plccCardRewardsRequest = plccCardRewardsRequest,
                plccCardRewardsSummary = plccCardTypeLimitSummary,
                now = now
            )

            // 혜택 save
            plccCardRewardsList.forEach { plccCardRewards ->
                plccCardRewardsConvertService.setScaleRewards(plccCardRewards)
                if (!plccCardRewards.benefitCode.equals("CXXX")) {
                    upsertRewardsTypeLimit(
                        executionContext,
                        rpcRequest,
                        plccCardRewardsRequest,
                        plccCardRewards,
                        now
                    )
                }
            }
        }
    }

    private fun upsertRewardsTypeLimitSummary(
        executionContext: CollectExecutionContext,
        rpcRequest: PlccRpcRequest,
        plccCardRewardsRequest: PlccCardRewardsRequest,
        plccCardRewardsSummary: PlccCardRewardsSummary?,
        now: LocalDateTime
    ) {
        val prevEntity =
            plccCardRewardsSummaryRepository.findByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardIdAndBenefitYearMonth(
                banksaladUserId = executionContext.userId.toLong(),
                cardCompanyId = executionContext.organizationId,
                cardCompanyCardId = rpcRequest.cardId,
                benefitYearMonth = plccCardRewardsRequest.dataBody?.inquiryYearMonth ?: ""
            )

        val newEntity = PlccCardRewardsUtil.makePlccCardRewardsSummaryEntity(
            banksaladUserId = executionContext.userId.toLong(),
            cardCompanyId = executionContext.organizationId,
            cardId = rpcRequest.cardId,
            benefitYearMonth = plccCardRewardsRequest.dataBody?.inquiryYearMonth,
            plccCardRewardsSummary = plccCardRewardsSummary,
            now = now
        )

        /** ConstraintViolationException발생 시 로그 찍고 그대로 publish 진행
         *  : 처음 연속 요청 시 Duplicate error 발생 가능성
         */
        try {
            // 없다면 insert
            if (prevEntity == null) {
                plccCardRewardsSummaryRepository.save(newEntity)
                return
            }
        } catch (e: DataIntegrityViolationException) {
            PlccCardRewardsServiceImpl.logger.Error(
                "[PLCC][실시간API] serviceID: {} serviceName: {} lotte tokenID: {} error message : {} stackTrace = {}",
                "PlccCardRewardsService",
                "롯데카드 혜택 종합(summary) 로직 Duplicate error",
                rpcRequest.cardId,
                e.message,
                e.stackTrace
            )
        }

        prevEntity?.let {
            // 있지만, 값의 차이가 없을 때 lastCheckAt만 업데이트
            if (prevEntity.equal(newEntity)) {
                prevEntity.apply {
                    this.lastCheckAt = now
                }.let {
                    plccCardRewardsSummaryRepository.save(it)
                }
                return
            }
            // 있지만, 값의 차이가 있을 때 변경된 값으로 저장
            newEntity.apply {
                plccCardBenefitLimitDetailSummaryId = prevEntity.plccCardBenefitLimitDetailSummaryId
                createdAt = prevEntity.createdAt
                updatedAt = prevEntity.updatedAt
            }.let {
                plccCardRewardsSummaryRepository.save(it)
            }
        }
    }

    private fun upsertRewardsTypeLimit(
        executionContext: CollectExecutionContext,
        rpcRequest: PlccRpcRequest,
        plccCardRewardsRequest: PlccCardRewardsRequest,
        plccCardRewards: PlccCardRewards,
        now: LocalDateTime
    ) {
        val prevEntity =
            plccCardRewardsRepository.findByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardIdAndBenefitYearMonthAndBenefitCode(
                banksaladUserId = executionContext.userId.toLong(),
                cardCompanyId = executionContext.organizationId,
                cardCompanyCardId = rpcRequest.cardId,
                benefitYearMonth = plccCardRewardsRequest.dataBody?.inquiryYearMonth ?: "",
                benefitCode = plccCardRewards.benefitCode ?: ""
            )

        val newEntity = PlccCardRewardsUtil.makePlccCardRewardsEntity(
            banksaladUserId = executionContext.userId.toLong(),
            cardCompanyId = executionContext.organizationId,
            cardCompanyCardId = rpcRequest.cardId,
            benefitYearMonth = plccCardRewardsRequest.dataBody?.inquiryYearMonth,
            plccCardRewards = plccCardRewards,
            now = now
        )

        /** ConstraintViolationException발생 시 로그 찍고 그대로 publish 진행
         *  : 처음 연속 요청 시 Duplicate error 발생 가능성
         */
        // 없다면 insert
        try {
            if (prevEntity == null) {
                plccCardRewardsRepository.save(newEntity)
                plccCardRewardsHistoryRepository.save(
                    PlccCardRewardsUtil.makeRewardsHisotryEntity(newEntity)
                )
                return
            }
        } catch (e: DataIntegrityViolationException) {
            PlccCardRewardsServiceImpl.logger.Error(
                "[PLCC][실시간API] serviceID: {} serviceName: {} lotte tokenID: {} error message : {} stackTrace = {}",
                "PlccCardRewardsService",
                "롯데카드 혜택 저장 로직 Duplicate error",
                rpcRequest.cardId,
                e.message,
                e.stackTrace
            )
        }
        prevEntity?.let {
            // 있지만, 값의 차이가 없을 때 lastCheckAt만 업데이트
            if (prevEntity.equal(newEntity)) {
                prevEntity.apply {
                    lastCheckAt = now
                }.let {
                    plccCardRewardsRepository.save(it)
                }
                return
            }
            // 있지만, 값의 차이가 있을 때 변경된 값으로 저장
            newEntity.apply {
                plccCardBenefitLimitDetailId = prevEntity.plccCardBenefitLimitDetailId
                createdAt = prevEntity.createdAt
                updatedAt = prevEntity.updatedAt
            }.let {
                plccCardRewardsRepository.save(it)
                plccCardRewardsHistoryRepository.save(
                    PlccCardRewardsUtil.makeRewardsHisotryEntity(it)
                )
            }
        }
    }

    private fun decodeKoreanFields(executionResponse: ExecutionResponse<PlccCardRewardsResponse>) {
        // response_message, benefit_name
        val dataBody = executionResponse.response?.dataBody
        dataBody?.plccCardThreshold?.apply {
            this.responseMessage = encodeService.base64Decode(this.responseMessage, Charset.forName("MS949"))
        }

        dataBody?.plccCardRewardsSummary?.apply {
            this.responseMessage = encodeService.base64Decode(this.responseMessage, Charset.forName("MS949"))
        }

        dataBody?.plccCardRewardsList?.forEach { plccCardTypeLimit ->
            plccCardTypeLimit.benefitName =
                encodeService.base64Decode(plccCardTypeLimit.benefitName, Charset.forName("MS949"))
        }
    }
}
