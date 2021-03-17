package com.rainist.collectcard.plcc.cardrewards

import com.github.banksalad.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.plcc.cardrewards.dto.PlccRpcRequest
import com.rainist.collectcard.plcc.cardtransactions.convertStringYearMonth
import com.rainist.collectcard.plcc.common.db.repository.PlccCardThresholdRepository
import com.rainist.collectcard.plcc.common.db.repository.PlccCardTypeLimitRepository
import com.rainist.collectcard.plcc.common.util.PlccCardRewardsUtil
import com.rainist.common.util.DateTimeUtil
import org.springframework.stereotype.Service

@Service
class PlccCardRewardsPublishServiceImpl(
    val plccCardThresholdRepository: PlccCardThresholdRepository,
    val plccCardTypeLimitRepository: PlccCardTypeLimitRepository,
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

    override fun rewardsTypeLimitPublish(
        executionContext: CollectExecutionContext,
        rpcRequest: PlccRpcRequest
    ): CollectcardProto.ListPlccRewardsTypeLimitResponse {

        val requestYearMonth =
            DateTimeUtil.epochMilliSecondToKSTLocalDateTime(rpcRequest.requestMonthMs)
        val stringYearMonth = convertStringYearMonth(requestYearMonth)

        val typeLimits =
            plccCardTypeLimitRepository.findAllByBanksaladUserIdAndCardCompanyIdAndCardCompanyCardIdAndBenefitYearMonth(
                banksaladUserId = executionContext.userId.toLong(),
                cardCompanyId = executionContext.organizationId,
                cardCompanyCardId = rpcRequest.cardId,
                benefitYearMonth = stringYearMonth.yearMonth ?: ""
            )

        val protoTypeLimits = typeLimits.map { plccCardRewardsConvertService.toTypeLimitProto(it) }.toList()

        return CollectcardProto.ListPlccRewardsTypeLimitResponse
            .newBuilder()
            .addAllRewardsTypeLimit(protoTypeLimits)
            .build()
    }
}
