package com.rainist.collectcard.plcc.cardrewards

import com.github.banksalad.idl.apis.v1.collectcard.CollectcardProto
import com.github.banksalad.idl.apis.v1.plcc.PlccProto
import com.google.protobuf.BoolValue
import com.google.protobuf.Int64Value
import com.google.protobuf.StringValue
import com.rainist.collectcard.plcc.common.db.entity.PlccCardThresholdEntity
import org.springframework.stereotype.Service

@Service
class PlccCardRewardsConvertService {
    fun toThresholdProto(entity: PlccCardThresholdEntity): CollectcardProto.GetPlccRewardsThresholdResponse {
        val newBuilder = CollectcardProto.GetPlccRewardsThresholdResponse.newBuilder()
        val rewardsThresholdBuilder = newBuilder.rewardsThresholdBuilder

        // epochMils로 변경 필요
        rewardsThresholdBuilder.rewardsStartsAtMs = entity.outcomeStartDay?.toLong() ?: 0L
        rewardsThresholdBuilder.rewardsEndsAtMs = entity.outcomeEndDay?.toLong() ?: 0L
        rewardsThresholdBuilder.isRewardsThresholdSuspended = BoolValue.of(entity.isOutcomeDelay ?: false)
        // 2f에 맞는 값으로 변경 필요
        rewardsThresholdBuilder.usedAmountBasedOnRewardsThreshold2F =
            entity.beforeMonthCriteriaUseAmount?.toLong() ?: 0L
        rewardsThresholdBuilder.minimumAmountForRewardsThreshold2F =
            Int64Value.of(entity.outcomeCriteriaAmount?.toLong() ?: 0L)
        rewardsThresholdBuilder.appliedRewardsAmount2F = entity.totalBenefitAmount?.toLong() ?: 0L
        rewardsThresholdBuilder.appliedRewardsCount = Int64Value.of(entity.totalBenefitCount?.toLong() ?: 0L)
        rewardsThresholdBuilder.totalSalesAmount2F = Int64Value.of(entity.totalSalesAmount?.toLong() ?: 0L)
        rewardsThresholdBuilder.earnedRewardsRate2F = Int64Value.of(entity.monthlyBenefitRate?.toLong() ?: 0L)
        rewardsThresholdBuilder.rewardsLimitAmount2F = entity.monthlyBenefitLimit?.toLong() ?: 0L
        rewardsThresholdBuilder.cashbackAmount2F = Int64Value.of(entity.cashbackAmount?.toLong() ?: 0L)
        rewardsThresholdBuilder.rewardsDetailMessage = StringValue.of(entity.benefitMessage)
        rewardsThresholdBuilder.promotionType = getPromotionType(entity.promotionCode)
        newBuilder.rewardsThreshold = rewardsThresholdBuilder.build()
        return newBuilder.build()
    }

    private fun getPromotionType(promotionCode: String?): PlccProto.RewardsPromotionType {
        // 0 or 1
        return when (promotionCode) {
            "0" -> PlccProto.RewardsPromotionType.REWARDS_PROMOTION_TYPE_NO_PROMOTION
            "1" -> PlccProto.RewardsPromotionType.REWARDS_PROMOTION_TYPE_ISSUED
            else -> PlccProto.RewardsPromotionType.REWARDS_PROMOTION_TYPE_UNKNOWN
        }
    }
}
