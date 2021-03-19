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
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsRequest
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsRequestDataBody
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsResponse
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardTypeLimit
import com.rainist.collectcard.plcc.cardrewards.dto.PlccRpcRequest
import com.rainist.collectcard.plcc.cardtransactions.convertStringYearMonth
import com.rainist.collectcard.plcc.common.db.repository.PlccCardTypeLimitHistoryRepository
import com.rainist.collectcard.plcc.common.db.repository.PlccCardTypeLimitRepository
import com.rainist.collectcard.plcc.common.util.PlccCardRewardsUtil
import com.rainist.common.log.Log
import com.rainist.common.service.ValidationService
import com.rainist.common.util.DateTimeUtil
import java.nio.charset.Charset
import java.time.LocalDateTime
import org.springframework.http.MediaType
import org.springframework.stereotype.Service

@Service
class PlccCardTypeLimitServiceImpl(
    val localDatetimeService: LocalDatetimeService,
    val headerService: HeaderService,
    val lottePlccExecutorService: CollectExecutorService,
    val validateService: ValidationService,
    val encodeService: EncodeService,
    val plccCardTypeLimitRepository: PlccCardTypeLimitRepository,
    val plccCardTypeLimitHistoryRepository: PlccCardTypeLimitHistoryRepository,
    val plccCardRewardsConvertService: PlccCardRewardsConvertService
) : PlccCardTypeLimitService {

    companion object : Log

    override fun listPlccCardTypeLimit(
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

        /* 혜택(RewardsTypeLimit) save */
        val plccCardThreshold = executionResponse.response?.dataBody?.plccCardThreshold
        if (plccCardThreshold?.responseCode.equals(ResultCode.OK.name)) {
            benefitList.forEach { plccCardRewardsTypeLimit ->
                plccCardRewardsConvertService.setScaleTypeLimit(plccCardRewardsTypeLimit)
                if (!plccCardRewardsTypeLimit.benefitCode.equals("CXXX")) {
                    upsertRewardsTypeLimit(
                        executionContext,
                        rpcRequest,
                        plccCardRewardsRequest,
                        plccCardRewardsTypeLimit,
                        now
                    )
                }
            }
        } else {
            logger.Warn("plcc response Code = {}", plccCardThreshold?.responseCode)
        }
    }

    private fun upsertRewardsTypeLimit(
        executionContext: CollectExecutionContext,
        rpcRequest: PlccRpcRequest,
        plccCardRewardsRequest: PlccCardRewardsRequest,
        plccCardTypeLimit: PlccCardTypeLimit,
        now: LocalDateTime
    ) {
        val prevEntity =
            plccCardTypeLimitRepository.findByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardIdAndBenefitYearMonthAndBenefitCode(
                banksaladUserId = executionContext.userId.toLong(),
                cardCompanyId = executionContext.organizationId,
                cardCompanyCardId = rpcRequest.cardId,
                benefitYearMonth = plccCardRewardsRequest.dataBody?.inquiryYearMonth ?: "",
                benefitCode = plccCardTypeLimit.benefitCode ?: ""
            )

        val newEntity = PlccCardRewardsUtil.makePlccCardTypeLimitEntity(
            banksaladUserId = executionContext.userId.toLong(),
            cardCompanyId = executionContext.organizationId,
            cardCompanyCardId = rpcRequest.cardId,
            benefitYearMonth = plccCardRewardsRequest.dataBody?.inquiryYearMonth,
            plccCardTypeLimit = plccCardTypeLimit,
            now = now
        )

        // 없다면 insert
        if (prevEntity == null) {
            plccCardTypeLimitRepository.save(newEntity)
            plccCardTypeLimitHistoryRepository.save(
                PlccCardRewardsUtil.makeTypeLimitHisotryEntity(newEntity)

            )
            // 있지만, 값의 차이가 없을 때 lastCheckAt만 업데이트
        } else if (prevEntity.equal(newEntity)) {
            prevEntity.apply {
                lastCheckAt = now
            }.let {
                plccCardTypeLimitRepository.save(it)
            }
        } else {
            // 있지만, 값의 차이가 있을 때 변경된 값으로 저장
            newEntity.apply {
                plccCardBenefitLimitDetailId = prevEntity.plccCardBenefitLimitDetailId
                createdAt = prevEntity.createdAt
                updatedAt = prevEntity.updatedAt
            }.let {
                plccCardTypeLimitRepository.save(it)
                plccCardTypeLimitHistoryRepository.save(
                    PlccCardRewardsUtil.makeTypeLimitHisotryEntity(it)
                )
            }
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
}
