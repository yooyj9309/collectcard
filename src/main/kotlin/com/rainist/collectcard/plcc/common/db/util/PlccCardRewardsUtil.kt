package com.rainist.collectcard.plcc.common.util

import com.rainist.collectcard.plcc.common.db.entity.PlccCardThresholdEntity
import com.rainist.collectcard.plcc.common.db.entity.PlccCardThresholdHistoryEntity
import com.rainist.collectcard.plcc.common.db.entity.PlccCardTypeLimitEntity
import com.rainist.collectcard.plcc.common.db.entity.PlccCardTypeLimitHistoryEntity

class PlccCardRewardsUtil {

    companion object {
        fun makeThresholdHisotryEntity(entity: PlccCardThresholdEntity): PlccCardThresholdHistoryEntity {
            return PlccCardThresholdHistoryEntity().apply {
                this.plccCardBenefitLimitId = entity.plccCardBenefitLimitId
                this.banksaladUserId = entity.banksaladUserId
                this.cardCompanyId = entity.cardCompanyId
                this.cardCompanyCardId = entity.cardCompanyCardId
                this.benefitYearMonth = entity.benefitYearMonth
                this.outcomeStartDay = entity.outcomeStartDay
                this.outcomeEndDay = entity.outcomeEndDay
                this.isOutcomeDelay = entity.isOutcomeDelay
                this.beforeMonthCriteriaUseAmount = entity.beforeMonthCriteriaUseAmount
                this.outcomeCriteriaAmount = entity.outcomeCriteriaAmount
                this.totalBenefitAmount = entity.totalBenefitAmount
                this.totalBenefitCount = entity.totalBenefitCount
                this.totalSalesAmount = entity.totalSalesAmount
                this.monthlyBenefitRate = entity.monthlyBenefitRate
                this.monthlyBenefitAmount = entity.monthlyBenefitAmount
                this.monthlyBenefitLimit = entity.monthlyBenefitLimit
                this.cashbackAmount = entity.cashbackAmount
                this.benefitMessage = entity.benefitMessage
                this.promotionCode = entity.promotionCode
                this.lastCheckAt = entity.lastCheckAt
                this.createdAt = entity.createdAt
                this.updatedAt = entity.updatedAt
            }
        }

        fun makeTypeLimitHisotryEntity(entity: PlccCardTypeLimitEntity): PlccCardTypeLimitHistoryEntity {
            return PlccCardTypeLimitHistoryEntity().apply {
                this.plccCardBenefitLimitDetailId = entity.plccCardBenefitLimitDetailId
                this.banksaladUserId = entity.banksaladUserId
                this.cardCompanyId = entity.cardCompanyId
                this.cardCompanyCardId = entity.cardCompanyCardId
                this.benefitYearMonth = entity.benefitYearMonth
                this.outcomeStartDay = entity.outcomeStartDay
                this.outcomeEndDay = entity.outcomeEndDay
                this.benefitName = entity.benefitName
                this.benefitCode = entity.benefitCode
                this.discountAmount = entity.discountAmount
                this.discountRate = entity.discountRate
                this.totalLimitAmount = entity.totalLimitAmount
                this.appliedAmount = entity.appliedAmount
                this.limitRemainingAmount = entity.limitRemainingAmount
                this.totalLimitCount = entity.totalLimitCount
                this.appliedCount = entity.appliedCount
                this.limitRemainingCount = entity.limitRemainingCount
                this.totalSalesLimitAmount = entity.totalSalesLimitAmount
                this.appliedSaleAmount = entity.appliedSaleAmount
                this.limitRemainingSalesAmount = entity.limitRemainingSalesAmount
                this.serviceType = entity.serviceType
                this.lastCheckAt = entity.lastCheckAt
                this.createdAt = entity.createdAt
                this.updatedAt = entity.updatedAt
            }
        }
    }
}
