package com.rainist.collectcard.plcc.common.util

import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewards
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsSummary
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardThreshold
import com.rainist.collectcard.plcc.common.db.entity.PlccCardRewardsEntity
import com.rainist.collectcard.plcc.common.db.entity.PlccCardRewardsHistoryEntity
import com.rainist.collectcard.plcc.common.db.entity.PlccCardRewardsSummaryEntity
import com.rainist.collectcard.plcc.common.db.entity.PlccCardThresholdEntity
import com.rainist.collectcard.plcc.common.db.entity.PlccCardThresholdHistoryEntity
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.YearMonth

class PlccCardRewardsUtil {

    companion object {
        fun makeThresholdEntity(
            banksaladUserId: Long,
            cardCompanyId: String,
            cardId: String,
            inquiryYearMonth: String?,
            threshold: PlccCardThreshold?,
            now: LocalDateTime
        ): PlccCardThresholdEntity {
            return PlccCardThresholdEntity().also { entity ->
                entity.banksaladUserId = banksaladUserId
                entity.cardCompanyId = cardCompanyId
                entity.cardCompanyCardId = cardId
                // inquiryYearMonth에서 1달 빼서 저장
                entity.benefitYearMonth = minusAMonth(inquiryYearMonth)
                entity.outcomeStartDay = threshold?.outcomeStartDate ?: ""
                entity.outcomeEndDay = threshold?.outcomeEndDate ?: ""
                entity.isOutcomeDelay = threshold?.isOutcomeDelay
                // not null
                entity.beforeMonthCriteriaUseAmount =
                    threshold?.beforeMonthCriteriaUseAmount ?: BigDecimal("0.0000")
                entity.outcomeCriteriaAmount = threshold?.outcomeCriteriaAmount
                entity.responseCode = threshold?.responseCode
                entity.responseMessage = threshold?.responseMessage
                entity.lastCheckAt = now
            }
        }

        fun makeThresholdHistoryEntity(entity: PlccCardThresholdEntity): PlccCardThresholdHistoryEntity {
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
                this.responseCode = entity.responseCode
                this.responseMessage = entity.responseMessage
                this.lastCheckAt = entity.lastCheckAt
                this.createdAt = entity.createdAt
                this.updatedAt = entity.updatedAt
                this.lastCheckAt = entity.lastCheckAt
            }
        }

        fun makePlccCardRewardsSummaryEntity(
            banksaladUserId: Long,
            cardCompanyId: String,
            cardId: String,
            benefitYearMonth: String?,
            plccCardRewardsSummary: PlccCardRewardsSummary?,
            now: LocalDateTime
        ): PlccCardRewardsSummaryEntity {
            return PlccCardRewardsSummaryEntity().also { entity ->
                entity.banksaladUserId = banksaladUserId
                entity.cardCompanyId = cardCompanyId
                entity.cardCompanyCardId = cardId
                entity.benefitYearMonth = benefitYearMonth
                // not null
                entity.totalBenefitAmount = plccCardRewardsSummary?.totalBenefitAmount ?: BigDecimal("0.0000")
                entity.totalBenefitCount = plccCardRewardsSummary?.totalBenefitCount
                entity.totalSalesAmount = plccCardRewardsSummary?.totalSalesAmount
                entity.monthlyBenefitRate = plccCardRewardsSummary?.monthlyBenefitRate
                // 롯데카드에서 주지 않는 데이터, 다른 회사 추가될 때 필요할 수 있어 추가.
                entity.monthlyBenefitAmount = null
                // not null
                entity.monthlyBenefitLimit = plccCardRewardsSummary?.monthlyBenefitLimit ?: BigDecimal("0.0000")
                entity.cashbackAmount = plccCardRewardsSummary?.cashbackAmount
                entity.benefitMessage = plccCardRewardsSummary?.message
                entity.promotionCode = plccCardRewardsSummary?.promotionCode?.name.toString()
                entity.responseCode = plccCardRewardsSummary?.responseCode
                entity.responseMessage = plccCardRewardsSummary?.responseMessage
                entity.lastCheckAt = now
            }
        }

        fun makePlccCardRewardsEntity(
            banksaladUserId: Long,
            cardCompanyId: String,
            cardCompanyCardId: String,
            benefitYearMonth: String?,
            plccCardRewards: PlccCardRewards,
            now: LocalDateTime
        ): PlccCardRewardsEntity {
            return PlccCardRewardsEntity().also { entity ->
                entity.banksaladUserId = banksaladUserId
                entity.cardCompanyId = cardCompanyId
                entity.cardCompanyCardId = cardCompanyCardId
                entity.benefitYearMonth = benefitYearMonth
                // not null
                entity.benefitName = plccCardRewards.benefitName ?: ""
                entity.benefitCode = plccCardRewards.benefitCode
                entity.discountAmount = null
                entity.discountRate = plccCardRewards.discountRate
                // not null
                entity.totalLimitAmount = plccCardRewards.totalLimitAmount ?: BigDecimal("0.0000")
                // not null
                entity.appliedAmount = plccCardRewards.appliedAmount ?: BigDecimal("0.0000")
                // not null
                entity.limitRemainingAmount = plccCardRewards.limitRemainingAmount ?: BigDecimal("0.0000")
                entity.totalLimitCount = plccCardRewards.totalLimitCount
                entity.appliedCount = plccCardRewards.appliedCount
                entity.limitRemainingCount = plccCardRewards.limitRemainingCount
                entity.totalSalesLimitAmount = plccCardRewards.totalSalesLimitAmount
                entity.appliedSaleAmount = plccCardRewards.appliedSalesAmount
                entity.limitRemainingSalesAmount = plccCardRewards.limitRemainingSalesAmount
                entity.serviceType = plccCardRewards.serviceType?.name
                entity.lastCheckAt = now
            }
        }

        fun makeRewardsHisotryEntity(entity: PlccCardRewardsEntity): PlccCardRewardsHistoryEntity {
            return PlccCardRewardsHistoryEntity().apply {
                this.plccCardBenefitLimitDetailId = entity.plccCardBenefitLimitDetailId
                this.banksaladUserId = entity.banksaladUserId
                this.cardCompanyId = entity.cardCompanyId
                this.cardCompanyCardId = entity.cardCompanyCardId
                this.benefitYearMonth = entity.benefitYearMonth
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
                this.lastCheckAt = entity.lastCheckAt
            }
        }

        fun minusAMonth(inquiryYearMonth: String?): String {
            // 2 ~ 12월이면 -1
            // 1월이면 이전 해의 12월로
            val parsedDate = SimpleDateFormat("yyyyMM").parse(inquiryYearMonth)
            val formatedDate = SimpleDateFormat("yyyy-MM").format(parsedDate)

            val localDate = YearMonth.parse(formatedDate)
            val month = localDate.month.value
            return if (month > 10) {
                "${localDate.year}${month - 1}" //  10, 11
            } else if (month in 2..10) {
                "${localDate.year}0${month - 1}" // 1, 2, 3, 4, 5, 6, 7, 8, 9
            } else {
                "${localDate.year - 1}12" // 12
            }
        }
    }
}
