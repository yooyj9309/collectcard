package com.rainist.collectcard.plcc.cardrewards

import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.service.EncodeService
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.service.LocalDatetimeService
import com.rainist.collectcard.grpc.handler.CollectcardGrpcService
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsRequest
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsRequestDataBody
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsResponse
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardThreshold
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
    val collectExecutorService: CollectExecutorService,
    val validateService: ValidationService,
    val encodeService: EncodeService,
    val plccCardTypeLimitRepository: PlccCardTypeLimitRepository,
    val plccCardTypeLimitHistoryRepository: PlccCardTypeLimitHistoryRepository
) : PlccCardTypeLimitService {

    companion object : Log

    override fun getPlccCardTypeLimit(
        executionContext: CollectExecutionContext,
        rpcRequest: PlccRpcRequest
    ): List<PlccCardTypeLimit> {

        val now = localDatetimeService.generateNowLocalDatetime().now

        // TODO (hyunjun) : 불필요한 코드, 테스트용 log.
        // val banksaladUserId = executionContext.userId.toLong()
        // val organizationId = executionContext.organizationId
        // val organization = organizationService.getOrganizationByOrganizationId(organizationId)

        CollectcardGrpcService.logger.Warn("PLCC rewardsRequest executionContext = {}", executionContext)

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

        // TODO Log 삭제
        CollectcardGrpcService.logger.Warn("PLCC rewardsRequest request : {}", plccCardRewardsRequest)

        val executionRequest = ExecutionRequest.builder<PlccCardRewardsRequest>()
            .headers(
                headerService.makeHeader(MediaType.APPLICATION_JSON_VALUE)
            )
            .request(
                plccCardRewardsRequest
            )
            .build()

        // TODO Log 삭제
        CollectcardGrpcService.logger.Warn("PLCC rewardsRequest executionRequest : {}", executionRequest)

        // api call
        val executionResponse: ExecutionResponse<PlccCardRewardsResponse> =
            collectExecutorService.execute(executionContext, execution, executionRequest)

        // TODO Log 삭제
        CollectcardGrpcService.logger.Warn("PLCC rewardsRequest response : {}", executionResponse.response)

        decodeKoreanFields(executionResponse)

        // TODO : DTO에 validation 어노테이션 추가 필요
        val benefitList = (executionResponse.response?.dataBody?.benefitList?.mapNotNull { plccCardRewardsTypeLimit ->
            validateService.validateOrNull(plccCardRewardsTypeLimit)
        }?.toMutableList()
            ?: mutableListOf())

        val rewardsThreshold = executionResponse.response?.dataBody?.plccCardThreshold

        /* 혜택(RewardsTypeLimit) save */
        benefitList.forEach { plccCardRewardsTypeLimit ->
            upsertRewardsTypeLimit(
                executionContext,
                rpcRequest,
                plccCardRewardsRequest,
                plccCardRewardsTypeLimit,
                rewardsThreshold,
                now
            )
        }

        return benefitList
    }

    private fun upsertRewardsTypeLimit(
        executionContext: CollectExecutionContext,
        rpcRequest: PlccRpcRequest,
        plccCardRewardsRequest: PlccCardRewardsRequest,
        plccCardTypeLimit: PlccCardTypeLimit,
        plccCardThreshold: PlccCardThreshold?,
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
            outcomeStartDay = plccCardThreshold?.outcomeStartDate ?: "",
            outcomeEndDay = plccCardThreshold?.outcomeEndDate ?: "",
            plccCardTypeLimit = plccCardTypeLimit,
            now = now
        )

        logger.Warn("prevEntity = {}", prevEntity)
        logger.Warn("newEntity = {}", newEntity)

        // 없다면 insert
        if (prevEntity == null) {
            plccCardTypeLimitRepository.save(newEntity)
            plccCardTypeLimitHistoryRepository.save(
                PlccCardRewardsUtil.makeTypeLimitHisotryEntity(newEntity)
            )
            return
        }

        // 있지만, 값의 차이가 없을 때 lastCheckAt만 업데이트
        if (prevEntity.equal(newEntity)) {
            prevEntity.apply {
                lastCheckAt = now
            }.let {
                plccCardTypeLimitRepository.save(it)
            }
            return
        }

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
