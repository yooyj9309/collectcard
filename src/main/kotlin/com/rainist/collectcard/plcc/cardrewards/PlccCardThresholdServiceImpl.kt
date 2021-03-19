package com.rainist.collectcard.plcc.cardrewards

import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.enums.ResultCode
import com.rainist.collectcard.common.service.EncodeService
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.service.LocalDatetimeService
import com.rainist.collectcard.common.service.OrganizationService
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsRequest
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsRequestDataBody
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsResponse
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardThreshold
import com.rainist.collectcard.plcc.cardrewards.dto.PlccRpcRequest
import com.rainist.collectcard.plcc.cardtransactions.convertStringYearMonth
import com.rainist.collectcard.plcc.common.db.repository.PlccCardThresholdHistoryRepository
import com.rainist.collectcard.plcc.common.db.repository.PlccCardThresholdRepository
import com.rainist.collectcard.plcc.common.util.PlccCardRewardsUtil
import com.rainist.common.log.Log
import com.rainist.common.service.ValidationService
import com.rainist.common.util.DateTimeUtil
import java.nio.charset.Charset
import java.time.LocalDateTime
import org.springframework.http.MediaType
import org.springframework.stereotype.Service

@Service
class PlccCardThresholdServiceImpl(
    val organizationService: OrganizationService,
    val headerService: HeaderService,
    val lottePlccExecutorService: CollectExecutorService,
    val validateService: ValidationService,
    val localDatetimeService: LocalDatetimeService,
    val plccCardThresholdRepository: PlccCardThresholdRepository,
    val plccCardThresholdHistoryRepository: PlccCardThresholdHistoryRepository,
    val plccCardRewardsConvertService: PlccCardRewardsConvertService,
    val encodeService: EncodeService
) : PlccCardThresholdService {

    companion object : Log

    override fun getPlccCardThreshold(
        executionContext: CollectExecutionContext,
        rpcRequest: PlccRpcRequest
    ) {

        val now = localDatetimeService.generateNowLocalDatetime().now

        // execution
        val execution = Executions.valueOf(
            BusinessType.plcc,
            Organization.lottecard,
            Transaction.plccCardReward
        )

        // request
        val plccCardRewardsRequest = PlccCardRewardsRequest().apply {
            val requestYearMonth =
                DateTimeUtil.epochMilliSecondToKSTLocalDateTime(rpcRequest.requestMonthMs)
            val stringYearMonth = convertStringYearMonth(requestYearMonth)

            this.dataBody = PlccCardRewardsRequestDataBody().apply {
                this.inquiryYearMonth = stringYearMonth.yearMonth
                this.cardNumber = rpcRequest.cardId
            }
        }

        val executionRequest = ExecutionRequest.builder<PlccCardRewardsRequest>()
            .headers(
                headerService.makeHeader(MediaType.APPLICATION_JSON_VALUE)
            )
            .request(
                plccCardRewardsRequest
            )
            .build()

        // api call
        val executionResponse: ExecutionResponse<PlccCardRewardsResponse> =
            lottePlccExecutorService.execute(executionContext, execution, executionRequest)

        decodeKoreanFields(executionResponse)

        // TODO : DTO에 validation 어노테이션 추가 필요
        val benefitList = (executionResponse.response?.dataBody?.benefitList?.mapNotNull { plccCardRewardsTypeLimit ->
            validateService.validateOrNull(plccCardRewardsTypeLimit)
        }?.toMutableList()
            ?: mutableListOf())

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
        } else {
            logger.Warn("plcc response Code = {}", rewardsThreshold?.responseCode)
        }
    }

    private fun decodeKoreanFields(executionResponse: ExecutionResponse<PlccCardRewardsResponse>) {
        val encodingflag = true
        if (encodingflag) {
            // response_message, benefit_name
            executionResponse.response?.dataBody?.plccCardThreshold?.apply {
                this.responseMessage = encodeService.base64Decode(this.responseMessage, Charset.forName("MS949"))
            }

            executionResponse.response?.dataBody?.benefitList?.forEach { plccCardTypeLimit ->
                plccCardTypeLimit.benefitName =
                    encodeService.base64Decode(plccCardTypeLimit.benefitName, Charset.forName("MS949"))
            }
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
            // 있지만 데이터가 같다면 lastCheckAt만 업데이트
        } else if (prevEntity.equal(newEntity)) {
            prevEntity.apply {
                lastCheckAt = now
            }.let {
                plccCardThresholdRepository.save(it)
            }
            // 있지만 데이터가 다르다면 prevEntity를 새로 만든 엔티티의 값으로 변경 후 저장, history insert
        } else {
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
