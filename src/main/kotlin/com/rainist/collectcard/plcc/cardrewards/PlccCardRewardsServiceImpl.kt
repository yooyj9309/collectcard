package com.rainist.collectcard.plcc.cardrewards

import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.service.LocalDatetimeService
import com.rainist.collectcard.common.service.OrganizationService
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsRequest
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsRequestDataBody
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsRequestDataHeader
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsResponse
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsThreshold
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsTypeLimit
import com.rainist.collectcard.plcc.cardrewards.dto.PlccRpcRequest
import com.rainist.collectcard.plcc.cardtransactions.convertStringYearMonth
import com.rainist.collectcard.plcc.common.db.repository.PlccCardThresholdHistoryRepository
import com.rainist.collectcard.plcc.common.db.repository.PlccCardThresholdRepository
import com.rainist.collectcard.plcc.common.db.repository.PlccCardTypeLimitHistoryRepository
import com.rainist.collectcard.plcc.common.db.repository.PlccCardTypeLimitRepository
import com.rainist.collectcard.plcc.common.util.PlccCardRewardsUtil
import com.rainist.common.service.ValidationService
import com.rainist.common.util.DateTimeUtil
import java.time.LocalDateTime
import org.springframework.http.MediaType
import org.springframework.stereotype.Service

@Service
class PlccCardRewardsServiceImpl(
    val organizationService: OrganizationService,
    val headerService: HeaderService,
    val collectExecutorService: CollectExecutorService,
    val validateService: ValidationService,
    val localDatetimeService: LocalDatetimeService,
    val plccCardThresholdRepository: PlccCardThresholdRepository,
    val plccCardThresholdHistoryRepository: PlccCardThresholdHistoryRepository,
    val plccCardTypeLimitRepository: PlccCardTypeLimitRepository,
    val plccCardTypeLimitHistoryRepository: PlccCardTypeLimitHistoryRepository
) : PlccCardRewardsService {

    override fun getPlccCardRewards(
        executionContext: CollectExecutionContext,
        rpcRequest: PlccRpcRequest
    ): PlccCardRewardsResponse {

        val now = localDatetimeService.generateNowLocalDatetime().now

        val banksaladUserId = executionContext.userId.toLong()
        val organizationId = executionContext.organizationId
        val organization = organizationService.getOrganizationByOrganizationId(organizationId)

        // execution
        val execution = Executions.valueOf(
            BusinessType.plcc,
            Organization.lottecard,
            Transaction.cardTransaction
        )

        // request
        val plccCardRewardsRequest = PlccCardRewardsRequest().apply {
            this.dataHeader = PlccCardRewardsRequestDataHeader()

            val requestYearMonth =
                DateTimeUtil.epochMilliSecondToKSTLocalDateTime(rpcRequest.requestMonthMs.toLong())
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
            collectExecutorService.execute(executionContext, execution, executionRequest)

        // TODO : DTO에 validation 어노테이션 추가 필요
        val benefitList = (executionResponse.response?.dataBody?.benefitList?.mapNotNull { plccCardRewardsTypeLimit ->
            validateService.validateOrNull(plccCardRewardsTypeLimit)
        }?.toMutableList()
            ?: mutableListOf())

        /** 실적(RewardsThreshold)와 혜택(RewardsTypeLimit)의 테이블이
         *  나눠져있기 때문에 구분해서 저장
         */

        /* 실적(RewardsThreshold) save */
        // 실적 데이터 조회
        val rewardsThreshold = executionResponse.response?.dataBody?.plccCardPlccCardRewardsThreshold
        upsertRewardsThreshold(
            executionContext,
            rpcRequest,
            plccCardRewardsRequest,
            rewardsThreshold,
            now
        )

        /* 혜택(RewardsTypeLimit) save */
        // 없다면 insert
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

        return executionResponse.response.apply {
            dataBody?.benefitList = benefitList
        }
    }

    private fun upsertRewardsTypeLimit(
        executionContext: CollectExecutionContext,
        rpcRequest: PlccRpcRequest,
        plccCardRewardsRequest: PlccCardRewardsRequest,
        plccCardRewardsTypeLimit: PlccCardRewardsTypeLimit,
        plccCardRewardsThreshold: PlccCardRewardsThreshold?,
        now: LocalDateTime
    ) {
        val prevEntity =
            plccCardTypeLimitRepository.findByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardIdAndBenefitYearMonthAndBenefitCode(
                banksaladUserId = executionContext.userId.toLong(),
                cardCompanyId = executionContext.organizationId,
                cardCompanyCardId = rpcRequest.cardId,
                benefitYearMonth = plccCardRewardsRequest.dataBody?.inquiryYearMonth ?: "",
                benefitCode = plccCardRewardsTypeLimit.benefitCode ?: ""
            )

        val newEntity = plccCardRewardsTypeLimit.toEntity(
            banksaladUserId = executionContext.userId.toLong(),
            cardCompanyId = executionContext.organizationId,
            cardCompanyCardId = rpcRequest.cardId,
            benefitYearMonth = plccCardRewardsRequest.dataBody?.inquiryYearMonth,
            outcomeStartDay = plccCardRewardsThreshold?.outcomeStartDate ?: "",
            outcomeEndDay = plccCardRewardsThreshold?.outcomeEndDate ?: "",
            now = now
        )
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
                plccCardTypeLimitHistoryRepository.save(
                    PlccCardRewardsUtil.makeTypeLimitHisotryEntity(it)
                )
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

    private fun upsertRewardsThreshold(
        executionContext: CollectExecutionContext,
        rpcRequest: PlccRpcRequest,
        plccCardRewardsRequest: PlccCardRewardsRequest,
        rewardsThreshold: PlccCardRewardsThreshold?,
        now: LocalDateTime
    ) {
        val prevEntity =
            plccCardThresholdRepository.findByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardIdAndBenefitYearMonth(
                banksaladUserId = executionContext.userId.toLong(),
                cardCompanyId = executionContext.organizationId,
                cardCompanyCardId = rpcRequest.cardId,
                benefitYearMonth = plccCardRewardsRequest.dataBody?.inquiryYearMonth ?: ""
            )
        val newEntity =
            rewardsThreshold?.toEntity(
                executionContext.userId.toLong(),
                executionContext.organizationId,
                rpcRequest.cardId,
                plccCardRewardsRequest.dataBody?.inquiryYearMonth,
                now
            )

        // 없다면, insert
        if (prevEntity == null) {
            newEntity?.let {
                plccCardThresholdRepository.save(it)
                plccCardThresholdHistoryRepository.save(
                    PlccCardRewardsUtil.makeThresholdHisotryEntity(
                        it
                    )
                )
            }
            return
        }

        // 있지만 데이터가 같다면 lastCheckAt만 업데이트, history insert
        if (prevEntity?.equal(newEntity)) {
            prevEntity.apply {
                lastCheckAt = now
            }.let {
                plccCardThresholdRepository.save(it)
                plccCardThresholdHistoryRepository.save(
                    PlccCardRewardsUtil.makeThresholdHisotryEntity(it)
                )
            }
            return
        }

        // 있지만 데이터가 다르다면 prevEntity를 새로 만든 엔티티의 값으로 변경 후 저장, history insert
        newEntity?.apply {
            this.banksaladUserId = prevEntity?.banksaladUserId
            this.createdAt = prevEntity?.createdAt
            this.updatedAt = prevEntity?.updatedAt
        }?.let {
            plccCardThresholdRepository.save(it)
            plccCardThresholdHistoryRepository.save(
                PlccCardRewardsUtil.makeThresholdHisotryEntity(
                    it
                )
            )
        }
    }
}
