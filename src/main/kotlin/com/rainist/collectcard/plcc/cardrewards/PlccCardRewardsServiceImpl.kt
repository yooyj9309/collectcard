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
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.nio.charset.Charset
import java.time.LocalDateTime

@Service
class PlccCardRewardsServiceImpl(
    val localDatetimeService: LocalDatetimeService,
    val headerService: HeaderService,
    val lottePlccExecutorService: CollectExecutorService,
    val validateService: ValidationService,
    val encodeService: EncodeService,
    val plccCardRewardsRepository: PlccCardRewardsRepository,
    val plccCardRewardsHistoryRepository: PlccCardRewardsHistoryRepository,
    val plccCardRewardsSummaryRepository: PlccCardRewardsSummaryRepository,
    val plccCardRewardsConvertService: PlccCardRewardsConvertService
) : PlccCardRewardsService {

    companion object : Log

    override fun getPlccRewards(
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

        val plccCardRewardsList =
            (executionResponse.response?.dataBody?.plccCardRewardsList?.mapNotNull { plccCardRewards ->
                validateService.validateOrNull(plccCardRewards)
            }?.toMutableList()
                ?: mutableListOf())

        /* ?????? ??????(Rewards summary)??? ??????(Rewards) save */
        val plccCardTypeLimitSummary = executionResponse.response?.dataBody?.plccCardRewardsSummary
        if (plccCardTypeLimitSummary?.responseCode.equals(ResultCode.OK.name)) {

            // ?????? ?????? save
            plccCardRewardsConvertService.setScaleRewardsSummary(plccCardTypeLimitSummary)
            upsertRewardsTypeLimitSummary(
                executionContext = executionContext,
                rpcRequest = rpcRequest,
                plccCardRewardsRequest = plccCardRewardsRequest,
                plccCardRewardsSummary = plccCardTypeLimitSummary,
                now = now
            )

            // ?????? save
            plccCardRewardsList.forEach { plccCardRewards ->
                plccCardRewardsConvertService.setScaleRewards(plccCardRewards)
                if (!plccCardRewards.benefitCode.equals("CXXX")) {
                    try {
                        upsertRewardsTypeLimit(
                            executionContext,
                            rpcRequest,
                            plccCardRewardsRequest,
                            plccCardRewards,
                            now
                        )
                    } catch (e: DataIntegrityViolationException) {
                        logger.Error(
                            "[PLCC][?????????API] Error log : {}",
                            "PlccCardRewardsService",
                            "???????????? ?????? ??????(summary) ?????? Duplicate error",
                            rpcRequest.cardId,
                            e.message,
                            e.stackTrace
                        )
                    }
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

        /** ConstraintViolationException?????? ??? ?????? ?????? ????????? publish ??????
         *  : ?????? ?????? ?????? ??? Duplicate error ?????? ?????????
         */
        // ????????? insert
        try {
            if (prevEntity == null) {
                plccCardRewardsSummaryRepository.save(newEntity)
                return
            }
        } catch (e: DataIntegrityViolationException) {
            logger.Error(
                "[PLCC][?????????API] Error log : {}",
                "PlccCardRewardsService",
                "???????????? ?????? ??????(summary) ?????? Duplicate error",
                rpcRequest.cardId,
                e.message,
                e.stackTrace
            )
        }

        prevEntity?.let {
            // ?????????, ?????? ????????? ?????? ??? lastCheckAt??? ????????????
            if (prevEntity.equal(newEntity)) {
                prevEntity.apply {
                    this.lastCheckAt = now
                }.let {
                    plccCardRewardsSummaryRepository.save(it)
                }
                return
            }
            // ?????????, ?????? ????????? ?????? ??? ????????? ????????? ??????
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

        /** ConstraintViolationException?????? ??? ?????? ?????? ????????? publish ??????
         *  : ?????? ?????? ?????? ??? Duplicate error ?????? ?????????
         */
        // ????????? insert
        if (prevEntity == null) {
            plccCardRewardsRepository.save(newEntity)
            plccCardRewardsHistoryRepository.save(
                PlccCardRewardsUtil.makeRewardsHisotryEntity(newEntity)
            )
            return
        }
        prevEntity?.let {
            // ?????????, ?????? ????????? ?????? ??? lastCheckAt??? ????????????
            if (prevEntity.equal(newEntity)) {
                prevEntity.apply {
                    lastCheckAt = now
                }.let {
                    plccCardRewardsRepository.save(it)
                }
                return
            }
            // ?????????, ?????? ????????? ?????? ??? ????????? ????????? ??????
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
