package com.rainist.collectcard.plcc.cardrewards

import com.github.banksalad.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.plcc.cardrewards.dto.PlccRpcRequest
import com.rainist.collectcard.plcc.cardtransactions.convertStringYearMonth
import com.rainist.collectcard.plcc.common.db.repository.PlccCardRewardsRepository
import com.rainist.collectcard.plcc.common.db.repository.PlccCardRewardsSummaryRepository
import com.rainist.collectcard.plcc.common.db.repository.PlccCardThresholdRepository
import com.rainist.collectcard.plcc.common.util.PlccCardRewardsUtil
import com.rainist.common.util.DateTimeUtil
import org.springframework.stereotype.Service

@Service
class PlccCardRewardsPublishServiceImpl(
    val plccCardThresholdRepository: PlccCardThresholdRepository,
    val plccCardRewardsRepository: PlccCardRewardsRepository,
    val plccCardRewardsSummaryRepository: PlccCardRewardsSummaryRepository,
    val plccCardRewardsConvertService: PlccCardRewardsConvertService
) : PlccCardRewardsPublishService {

    override fun rewardsThresholdPublish(
        executionContext: CollectExecutionContext,
        rpcRequest: PlccRpcRequest
    ): CollectcardProto.GetPlccRewardsThresholdResponse {

        val requestYearMonth =
            DateTimeUtil.epochMilliSecondToKSTLocalDateTime(rpcRequest.requestMonthMs)
        val stringYearMonth = convertStringYearMonth(requestYearMonth)

        plccCardThresholdRepository.findByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardIdAndBenefitYearMonth(
            banksaladUserId = executionContext.userId.toLong(),
            cardCompanyId = executionContext.organizationId,
            cardCompanyCardId = rpcRequest.cardId,
            // inquiryYearMonth의 1달 전을 입력해야함.
            benefitYearMonth = PlccCardRewardsUtil.minusAMonth(stringYearMonth.yearMonth) ?: ""
        )?.let {
            return plccCardRewardsConvertService.toThresholdProto(it)
        }

        return CollectcardProto.GetPlccRewardsThresholdResponse
            .newBuilder()
            .build()
    }

    override fun rewardsPublish(
        executionContext: CollectExecutionContext,
        rpcRequest: PlccRpcRequest
    ): CollectcardProto.GetPlccRewardsResponse {

        val requestYearMonth =
            DateTimeUtil.epochMilliSecondToKSTLocalDateTime(rpcRequest.requestMonthMs)
        val stringYearMonth = convertStringYearMonth(requestYearMonth)

        // 혜택 summary 조회
        plccCardRewardsSummaryRepository.findByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardIdAndBenefitYearMonth(
            banksaladUserId = executionContext.userId.toLong(),
            cardCompanyId = executionContext.organizationId,
            cardCompanyCardId = rpcRequest.cardId,
            benefitYearMonth = stringYearMonth.yearMonth ?: ""
        )?.let { rewardsSummary ->
            // 혜택 조회
            val rewardsList =
                plccCardRewardsRepository.findAllByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardIdAndBenefitYearMonth(
                    banksaladUserId = executionContext.userId.toLong(),
                    cardCompanyId = executionContext.organizationId,
                    cardCompanyCardId = rpcRequest.cardId,
                    benefitYearMonth = stringYearMonth.yearMonth ?: ""
                )

            return plccCardRewardsConvertService.toRewardsProto(rewardsSummary, rewardsList)
        }

        return CollectcardProto.GetPlccRewardsResponse
            .newBuilder()
            .build()
    }
}
