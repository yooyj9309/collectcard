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
import com.rainist.collectcard.plcc.cardrewards.PlccCardThresholdServiceImpl
import com.rainist.collectcard.plcc.cardrewards.PlccCardThresholdServiceImpl.Companion.Error
import com.rainist.collectcard.plcc.cardrewards.PlccCardThresholdServiceImpl.Companion.Warn
import com.rainist.collectcard.plcc.cardrewards.RewardsApiMockSetting.Companion.makeRewardsRequest
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsRequest
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsRequestDataBody
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsResponse
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardThreshold
import com.rainist.collectcard.plcc.cardrewards.dto.PlccRpcRequest
import com.rainist.collectcard.plcc.cardtransactions.convertStringYearMonth
import com.rainist.collectcard.plcc.common.db.repository.PlccCardThresholdHistoryRepository
import com.rainist.collectcard.plcc.common.db.repository.PlccCardThresholdRepository
import com.rainist.collectcard.plcc.common.util.PlccCardRewardsUtil
import com.rainist.common.util.DateTimeUtil
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import java.nio.charset.Charset
import java.time.LocalDateTime

/** API 요청하는 부분을 mocking한 테스트용 클래스
 */
@Component
class TestPlccCardThresholdServiceImpl(
    val headerService: HeaderService,
    val lottePlccExecutorService: CollectExecutorService,
    val localDatetimeService: LocalDatetimeService,
    val plccCardThresholdRepository: PlccCardThresholdRepository,
    val plccCardThresholdHistoryRepository: PlccCardThresholdHistoryRepository,
    val plccCardRewardsConvertService: PlccCardRewardsConvertService,
    val encodeService: EncodeService
) {
    fun getPlccCardThreshold(
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
            executionRequest = makeRewardsRequest("202103")
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

        // dto를 setScale(4) 적용 : 엔티티와 정확한 비교를 위해
        plccCardRewardsConvertService.setScaleThreshold(executionResponse.response?.dataBody?.plccCardThreshold)

        /* 실적(RewardsThreshold) save */
        // 실적 데이터 조회
        val rewardsThreshold = executionResponse.response?.dataBody?.plccCardThreshold
        // response_code가 OK인 경우만 save로직 수행
        if (rewardsThreshold?.responseCode.equals(ResultCode.OK.name)) {
            upsertRewardsThreshold(
                executionContext,
                rpcRequest,
                plccCardRewardsRequest,
                rewardsThreshold,
                now
            )
        }
    }

    private fun decodeKoreanFields(executionResponse: ExecutionResponse<PlccCardRewardsResponse>) {
        // response_message, benefit_name
        executionResponse.response?.dataBody?.plccCardThreshold?.apply {
            this.responseMessage = encodeService.base64Decode(this.responseMessage, Charset.forName("MS949"))
        }

        executionResponse.response?.dataBody?.plccCardRewardsList?.forEach { plccCardRewards ->
            plccCardRewards.benefitName =
                encodeService.base64Decode(plccCardRewards.benefitName, Charset.forName("MS949"))
        }
    }

    private fun upsertRewardsThreshold(
        executionContext: CollectExecutionContext,
        rpcRequest: PlccRpcRequest,
        plccCardRewardsRequest: PlccCardRewardsRequest,
        rewardsThreshold: PlccCardThreshold?,
        now: LocalDateTime
    ) {
        val prevEntity =
            plccCardThresholdRepository.findByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardIdAndBenefitYearMonth(
                banksaladUserId = executionContext.userId.toLong(),
                cardCompanyId = executionContext.organizationId,
                cardCompanyCardId = rpcRequest.cardId,
                // inquiryYearMonth 1달 빼서 조회해야함.
                benefitYearMonth = PlccCardRewardsUtil.minusAMonth(plccCardRewardsRequest.dataBody?.inquiryYearMonth)
            )
        val newEntity =
            PlccCardRewardsUtil.makeThresholdEntity(
                executionContext.userId.toLong(),
                executionContext.organizationId,
                rpcRequest.cardId,
                plccCardRewardsRequest.dataBody?.inquiryYearMonth,
                rewardsThreshold,
                now
            )

        /** ConstraintViolationException발생 시 로그 찍고 그대로 publish 진행
         *  : 처음 연속 요청 시 Duplicate error 발생 가능성
         */
        try {
            // 없다면, insert
            if (prevEntity == null) {
                newEntity.let {
                    plccCardThresholdRepository.save(it)
                    plccCardThresholdHistoryRepository.save(
                        PlccCardRewardsUtil.makeThresholdHistoryEntity(
                            it
                        )
                    )
                }
                return
            }
        } catch (e: DataIntegrityViolationException) {
            PlccCardThresholdServiceImpl.logger.Warn("run?", "run")
            PlccCardThresholdServiceImpl.logger.Error(
                "[PLCC][실시간API] serviceID: {} serviceName: {} lotte tokenID: {} error message : {} stackTrace = {}",
                "PlccCardThreshold",
                "롯데카드 실적 저장 로직 Duplicate error",
                rpcRequest.cardId,
                e.message,
                e.stackTrace
            )
        }

        prevEntity?.let {
            // 있지만 데이터가 같다면 lastCheckAt만 업데이트
            if (prevEntity.equal(newEntity)) {
                prevEntity.apply {
                    lastCheckAt = now
                }.let {
                    plccCardThresholdRepository.save(it)
                }
                return
            }

            // 있지만 데이터가 다르다면 prevEntity를 새로 만든 엔티티의 값으로 변경 후 저장, history insert
            newEntity.apply {
                this.plccCardBenefitLimitId = prevEntity.plccCardBenefitLimitId
                this.createdAt = prevEntity.createdAt
                this.updatedAt = prevEntity.updatedAt
            }.let {
                plccCardThresholdRepository.save(it)
                plccCardThresholdHistoryRepository.save(
                    PlccCardRewardsUtil.makeThresholdHistoryEntity(
                        it
                    )
                )
            }
        }
    }
}
