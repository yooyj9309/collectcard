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
import com.rainist.common.util.DateTimeUtil
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.nio.charset.Charset
import java.time.LocalDateTime

@Service
class PlccCardThresholdServiceImpl(
    val organizationService: OrganizationService,
    val headerService: HeaderService,
    val lottePlccExecutorService: CollectExecutorService,
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

        // dto??? setScale(4) ?????? : ???????????? ????????? ????????? ??????
        plccCardRewardsConvertService.setScaleThreshold(executionResponse.response?.dataBody?.plccCardThreshold)

        /* ??????(RewardsThreshold) save */
        // ?????? ????????? ??????
        val rewardsThreshold = executionResponse.response?.dataBody?.plccCardThreshold
        // response_code??? OK??? ????????? save?????? ??????
        if (rewardsThreshold?.responseCode.equals(ResultCode.OK.name)) {
            try {
                upsertRewardsThreshold(
                    executionContext,
                    rpcRequest,
                    plccCardRewardsRequest,
                    rewardsThreshold,
                    now
                )
            } catch (e: DataIntegrityViolationException) {
                logger.Error(
                    "[PLCC][?????????API] Error log : {}",
                    "PlccCardThreshold",
                    "???????????? ?????? ?????? ?????? Duplicate error",
                    rpcRequest.cardId,
                    e.message,
                    e.stackTrace
                )
            }
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
                // inquiryYearMonth 1??? ?????? ???????????????.
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

        /** ConstraintViolationException?????? ??? ?????? ?????? ????????? publish ??????
         *  : ?????? ?????? ?????? ??? Duplicate error ?????? ?????????
         */
        // ?????????, insert
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
        prevEntity?.let {
            // ????????? ???????????? ????????? lastCheckAt??? ????????????
            if (prevEntity.equal(newEntity)) {
                prevEntity.apply {
                    lastCheckAt = now
                }.let {
                    plccCardThresholdRepository.save(it)
                }
                return
            }

            // ????????? ???????????? ???????????? prevEntity??? ?????? ?????? ???????????? ????????? ?????? ??? ??????, history insert
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
