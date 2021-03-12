package com.rainist.collectcard.plcc.cardrewards

import com.github.banksalad.idl.apis.v1.collectcard.CollectcardProto
import com.github.banksalad.idl.apis.v1.plcc.PlccProto
import com.google.protobuf.BoolValue
import com.google.protobuf.Int64Value
import com.google.protobuf.StringValue
import com.rainist.collectcard.plcc.common.db.entity.PlccCardThresholdEntity
import com.rainist.collectcard.plcc.common.db.entity.PlccCardTypeLimitEntity
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
        rewardsThresholdBuilder.appliedRewardsAmount2F = multiplyForAmount(entity.totalBenefitAmount)
        rewardsThresholdBuilder.appliedRewardsCount = Int64Value.of(entity.totalBenefitCount?.toLong() ?: 0L)
        rewardsThresholdBuilder.totalSalesAmount2F = Int64Value.of(multiplyForAmount(entity.totalSalesAmount))
        rewardsThresholdBuilder.earnedRewardsRate2F =
            Int64Value.of(multiplyForAmount(entity.monthlyBenefitRate))
        rewardsThresholdBuilder.rewardsLimitAmount2F = multiplyForAmount(entity.monthlyBenefitLimit)
        rewardsThresholdBuilder.cashbackAmount2F = Int64Value.of(multiplyForAmount(entity.cashbackAmount))
        rewardsThresholdBuilder.rewardsDetailMessage = StringValue.of(entity.benefitMessage)
        rewardsThresholdBuilder.promotionType = getPromotionType(entity.promotionCode)
        getPlccRewardsThresholdResponseBuilder.rewardsThreshold = rewardsThresholdBuilder.build()
        return getPlccRewardsThresholdResponseBuilder.build()
    }

    private fun convertEpochMils(outcomeDay: String?): Long {
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

    fun toTypeLimitProto(entity: PlccCardTypeLimitEntity): CollectcardProto.RewardsTypeLimit {
        val rewardsTypeLimit = CollectcardProto.RewardsTypeLimit.newBuilder()

        rewardsTypeLimit.rewardsTypeName = getRewardsTypeName(entity.benefitName)
        rewardsTypeLimit.rewardsCode = StringValue.of(entity.benefitCode)
        rewardsTypeLimit.rewardsLimitAmount2F = multiplyForAmount(entity.totalLimitAmount)
        rewardsTypeLimit.rewardsLimitUsedAmount2F = multiplyForAmount(entity.appliedAmount)
        rewardsTypeLimit.rewardsLimitRemainingAmount2F = multiplyForAmount(entity.limitRemainingAmount)

        rewardsTypeLimit.rewardsLimitCount = Int64Value.of(entity.totalLimitCount?.toLong() ?: 0L)
        rewardsTypeLimit.rewardsLimitUsedCount = Int64Value.of(entity.appliedCount?.toLong() ?: 0L)
        rewardsTypeLimit.rewardsLimitRemainingCount = Int64Value.of(entity.limitRemainingCount?.toLong() ?: 0L)

        rewardsTypeLimit.rewardsLimitSalesAmount2F =
            Int64Value.of(multiplyForAmount(entity.totalSalesLimitAmount))
        rewardsTypeLimit.rewardsLimitUsedSalesAmount2F =
            Int64Value.of(multiplyForAmount(entity.appliedSaleAmount))
        rewardsTypeLimit.rewardsLimitRemainingSalesAmount2F =
            Int64Value.of(multiplyForAmount(entity.limitRemainingSalesAmount))
        rewardsTypeLimit.serviceType = getServiceType(entity.serviceType)

        return rewardsTypeLimit.build()
    }

    private fun multiplyForAmount(amount: BigDecimal?): Long {
        return amount?.multiply(BigDecimal(100))?.toLong() ?: 0L
    }

    private fun getServiceType(serviceType: String?): PlccProto.RewardsServiceType {
        return when (serviceType) {
            "01" -> PlccProto.RewardsServiceType.REWARDS_SERVICE_TYPE_CHARGE_DISCOUNT
            "02" -> PlccProto.RewardsServiceType.REWARDS_SERVICE_TYPE_POINT
            "03" -> PlccProto.RewardsServiceType.REWARDS_SERVICE_TYPE_INSTALLMENT_REDUCT
            else -> PlccProto.RewardsServiceType.REWARDS_SERVICE_TYPE_UNKNOWN
        }
    }

    // TODO(hyunjun) : 혜택명은 데이터보고 확인.
    private fun getRewardsTypeName(benefitName: String?): PlccProto.RewardsType {
        return when (benefitName) {
            "CAFE" -> PlccProto.RewardsType.REWARDS_TYPE_CAFE_DISCOUNT
            "CONVENIENCE" -> PlccProto.RewardsType.REWARDS_TYPE_CONVENIENCE_STORE_DISCOUNT
            "DELIVERY" -> PlccProto.RewardsType.REWARDS_TYPE_DELIVERY_APP_DISCOUNT
            "STREAMING" -> PlccProto.RewardsType.REWARDS_TYPE_STREAMING_DISCOUNT
            else -> PlccProto.RewardsType.REWARDS_TYPE_UNKNOWN
        }
    }
}
