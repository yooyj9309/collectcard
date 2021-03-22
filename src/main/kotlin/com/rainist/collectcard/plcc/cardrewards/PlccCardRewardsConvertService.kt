package com.rainist.collectcard.plcc.cardrewards

import com.github.banksalad.idl.apis.v1.collectcard.CollectcardProto
import com.github.banksalad.idl.apis.v1.plcc.PlccProto
import com.google.protobuf.BoolValue
import com.google.protobuf.Int64Value
import com.google.protobuf.StringValue
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewards
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsSummary
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardThreshold
import com.rainist.collectcard.plcc.common.db.entity.PlccCardRewardsEntity
import com.rainist.collectcard.plcc.common.db.entity.PlccCardRewardsSummaryEntity
import com.rainist.collectcard.plcc.common.db.entity.PlccCardThresholdEntity
import com.rainist.common.util.DateTimeUtil
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDate
import org.springframework.stereotype.Service

@Service
class PlccCardRewardsConvertService {
    fun toThresholdProto(entity: PlccCardThresholdEntity): CollectcardProto.GetPlccRewardsThresholdResponse {
        val getPlccRewardsThresholdResponseBuilder = CollectcardProto.GetPlccRewardsThresholdResponse.newBuilder()
        val rewardsThresholdBuilder = getPlccRewardsThresholdResponseBuilder.rewardsThresholdBuilder

        rewardsThresholdBuilder.rewardsStartsAtMs = convertEpochMils(entity.outcomeStartDay)
        rewardsThresholdBuilder.rewardsEndsAtMs = convertEpochMils(entity.outcomeEndDay)
        rewardsThresholdBuilder.isRewardsThresholdSuspended = BoolValue.of(entity.isOutcomeDelay ?: false)
        rewardsThresholdBuilder.usedAmountBasedOnRewardsThreshold2F =
            multiplyForAmount(entity.beforeMonthCriteriaUseAmount)
        rewardsThresholdBuilder.minimumAmountForRewardsThreshold2F =
            Int64Value.of(multiplyForAmount(entity.outcomeCriteriaAmount))
        getPlccRewardsThresholdResponseBuilder.rewardsThreshold = rewardsThresholdBuilder.build()
        return getPlccRewardsThresholdResponseBuilder.build()
    }

    fun toRewardsProto(
        rewardsSummary: PlccCardRewardsSummaryEntity,
        rewardsList: List<PlccCardRewardsEntity>
    ): CollectcardProto.GetPlccRewardsResponse {
        val protoRewards = rewardsList.map { toEachRewardsProto(it) }.toList()

        return CollectcardProto.GetPlccRewardsResponse
            .newBuilder()
            .setAppliedRewardsAmount2F(multiplyForAmount(rewardsSummary.totalBenefitAmount))
            .setAppliedRewardsCount(Int64Value.of(rewardsSummary.totalBenefitCount?.toLong() ?: 0L))
            .setTotalSalesAmount2F(Int64Value.of(multiplyForAmount(rewardsSummary.totalSalesAmount)))
            .setEarnedRewardsRate2F(Int64Value.of(multiplyForAmount(rewardsSummary.monthlyBenefitRate)))
            .setRewardsLimitAmount2F(multiplyForAmount(rewardsSummary.monthlyBenefitLimit))
            .setCashbackAmount2F(Int64Value.of(multiplyForAmount(rewardsSummary.cashbackAmount)))
            .setRewardsDetailMessage(StringValue.of(rewardsSummary.benefitMessage))
            .setPromotionType(getPromotionType(rewardsSummary.promotionCode))
            .addAllRewardsTypeLimit(protoRewards)
            .build()
    }

    fun toEachRewardsProto(rewardsEntity: PlccCardRewardsEntity): CollectcardProto.RewardsTypeLimit {
        val rewardsTypeLimit = CollectcardProto.RewardsTypeLimit.newBuilder()

        rewardsTypeLimit.rewardsTypeName = getRewardsTypeName(rewardsEntity.benefitCode)
        rewardsTypeLimit.rewardsCode = StringValue.of(rewardsEntity.benefitCode)
        rewardsTypeLimit.rewardsLimitAmount2F = multiplyForAmount(rewardsEntity.totalLimitAmount)
        rewardsTypeLimit.rewardsLimitUsedAmount2F = multiplyForAmount(rewardsEntity.appliedAmount)
        rewardsTypeLimit.rewardsLimitRemainingAmount2F = multiplyForAmount(rewardsEntity.limitRemainingAmount)

        rewardsTypeLimit.rewardsLimitCount = Int64Value.of(rewardsEntity.totalLimitCount?.toLong() ?: 0L)
        rewardsTypeLimit.rewardsLimitUsedCount = Int64Value.of(rewardsEntity.appliedCount?.toLong() ?: 0L)
        rewardsTypeLimit.rewardsLimitRemainingCount = Int64Value.of(rewardsEntity.limitRemainingCount?.toLong() ?: 0L)

        rewardsTypeLimit.rewardsLimitSalesAmount2F =
            Int64Value.of(multiplyForAmount(rewardsEntity.totalSalesLimitAmount))
        rewardsTypeLimit.rewardsLimitUsedSalesAmount2F =
            Int64Value.of(multiplyForAmount(rewardsEntity.appliedSaleAmount))
        rewardsTypeLimit.rewardsLimitRemainingSalesAmount2F =
            Int64Value.of(multiplyForAmount(rewardsEntity.limitRemainingSalesAmount))
        rewardsTypeLimit.serviceType = getServiceType(rewardsEntity.serviceType)

        return rewardsTypeLimit.build()
    }

    private fun convertEpochMils(outcomeDay: String?): Long {
        if (outcomeDay.equals("")) {
            return 0L
        }
        val parsedDate = SimpleDateFormat("yyyyMMdd").parse(outcomeDay)
        val reformatDate = SimpleDateFormat("yyyy-MM-dd").format(parsedDate)
        return DateTimeUtil.kstLocalDateToEpochMilliSecond(LocalDate.parse(reformatDate))
    }

    private fun getPromotionType(promotionCode: String?): PlccProto.RewardsPromotionType {
        // 0 or 1
        return when (promotionCode) {
            "NO_PROMOTION" -> PlccProto.RewardsPromotionType.REWARDS_PROMOTION_TYPE_NO_PROMOTION
            "ISSUED" -> PlccProto.RewardsPromotionType.REWARDS_PROMOTION_TYPE_ISSUED
            else -> PlccProto.RewardsPromotionType.REWARDS_PROMOTION_TYPE_UNKNOWN
        }
    }

    private fun multiplyForAmount(amount: BigDecimal?): Long {
        return amount?.multiply(BigDecimal(100))?.toLong() ?: 0L
    }

    private fun getServiceType(serviceType: String?): PlccProto.RewardsServiceType {
        return when (serviceType) {
            "CHARGE_DISCOUNT" -> PlccProto.RewardsServiceType.REWARDS_SERVICE_TYPE_CHARGE_DISCOUNT
            "POINT" -> PlccProto.RewardsServiceType.REWARDS_SERVICE_TYPE_POINT
            "INSTALLMENT_REDUCT" -> PlccProto.RewardsServiceType.REWARDS_SERVICE_TYPE_INSTALLMENT_REDUCT
            else -> PlccProto.RewardsServiceType.REWARDS_SERVICE_TYPE_UNKNOWN
        }
    }

    private fun getRewardsTypeName(benefitCode: String?): PlccProto.RewardsType {
        return when (benefitCode) {
            "C292" -> PlccProto.RewardsType.REWARDS_TYPE_CAFE_DISCOUNT
            "C293" -> PlccProto.RewardsType.REWARDS_TYPE_DELIVERY_APP_DISCOUNT
            "C294" -> PlccProto.RewardsType.REWARDS_TYPE_STREAMING_DISCOUNT
            "C295" -> PlccProto.RewardsType.REWARDS_TYPE_CONVENIENCE_STORE_DISCOUNT
            else -> PlccProto.RewardsType.REWARDS_TYPE_UNKNOWN
        }
    }

    fun setScaleThreshold(threshold: PlccCardThreshold?) {
        threshold?.beforeMonthCriteriaUseAmount = threshold?.beforeMonthCriteriaUseAmount?.setScale(4)
        threshold?.outcomeCriteriaAmount = threshold?.outcomeCriteriaAmount?.setScale(4)
    }

    fun setScaleRewardsSummary(rewardsSummary: PlccCardRewardsSummary?) {
        rewardsSummary?.totalBenefitAmount = rewardsSummary?.totalBenefitAmount?.setScale(4)
        rewardsSummary?.totalSalesAmount = rewardsSummary?.totalSalesAmount?.setScale(4)
        rewardsSummary?.monthlyBenefitRate = rewardsSummary?.monthlyBenefitRate?.setScale(4)
        rewardsSummary?.monthlyBenefitLimit = rewardsSummary?.monthlyBenefitLimit?.setScale(4)
        rewardsSummary?.cashbackAmount = rewardsSummary?.cashbackAmount?.setScale(4)
    }

    fun setScaleRewards(rewards: PlccCardRewards) {
        rewards.discountRate = rewards.discountRate?.setScale(4)
        rewards.totalLimitAmount = rewards.totalLimitAmount?.setScale(4)
        rewards.appliedAmount = rewards.appliedAmount?.setScale(4)
        rewards.limitRemainingAmount = rewards.limitRemainingAmount?.setScale(4)
        rewards.totalSalesLimitAmount = rewards.totalSalesLimitAmount?.setScale(4)
        rewards.appliedSalesAmount = rewards.appliedSalesAmount?.setScale(4)
        rewards.limitRemainingSalesAmount = rewards.limitRemainingSalesAmount?.setScale(4)
    }
}
