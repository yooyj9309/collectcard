package com.rainist.collectcard.plcc.common.util

import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardThreshold
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardTypeLimit
import com.rainist.collectcard.plcc.common.db.entity.PlccCardThresholdEntity
import com.rainist.collectcard.plcc.common.db.entity.PlccCardThresholdHistoryEntity
import com.rainist.collectcard.plcc.common.db.entity.PlccCardTypeLimitEntity
import com.rainist.collectcard.plcc.common.db.entity.PlccCardTypeLimitHistoryEntity
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.YearMonth

class PlccCardRewardsUtil {

    companion object {
        fun makeThresholdEntity(
            banksaladUserId: Long,
            organizationId: String,
            cardId: String,
            inquiryYearMonth: String?,
            threshold: PlccCardThreshold?,
            now: LocalDateTime
        ): PlccCardThresholdEntity {
            return PlccCardThresholdEntity().also { entity ->
                entity.banksaladUserId = banksaladUserId
                entity.cardCompanyId = organizationId
                entity.cardCompanyCardId = cardId
                // inquiryYearMonth에서 1달 빼서 저장
                entity.benefitYearMonth = minusAMonth(inquiryYearMonth)
                entity.outcomeStartDay = threshold?.outcomeStartDate ?: ""
                entity.outcomeEndDay = threshold?.outcomeEndDate ?: ""
                entity.isOutcomeDelay = threshold?.isOutcomeDelay
                // not null
                entity.beforeMonthCriteriaUseAmount = threshold?.beforeMonthCriteriaUseAmount ?: BigDecimal("0.0000")
                entity.outcomeCriteriaAmount = threshold?.outcomeCriteriaAmount
                // not null
                entity.totalBenefitAmount = threshold?.totalBenefitAmount ?: BigDecimal("0.0000")
                entity.totalBenefitCount = threshold?.totalBenefitCount
                entity.totalSalesAmount = threshold?.totalSalesAmount
                entity.monthlyBenefitRate = threshold?.monthlyBenefitRate
                entity.monthlyBenefitAmount = null
                // not null
                entity.monthlyBenefitLimit = threshold?.monthlyBenefitLimit ?: BigDecimal("0.0000")
                entity.cashbackAmount = threshold?.cashbackAmount
                entity.benefitMessage = threshold?.message
                entity.promotionCode = threshold?.promotionCode?.name.toString()
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

        fun makePlccCardTypeLimitEntity(
            banksaladUserId: Long,
            cardCompanyId: String,
            cardCompanyCardId: String,
            benefitYearMonth: String?,
            outcomeStartDay: String,
            outcomeEndDay: String,
            plccCardTypeLimit: PlccCardTypeLimit,
            now: LocalDateTime
        ): PlccCardTypeLimitEntity {
            return PlccCardTypeLimitEntity().also { entity ->
                entity.banksaladUserId = banksaladUserId
                entity.cardCompanyId = cardCompanyId
                entity.cardCompanyCardId = cardCompanyCardId
                entity.benefitYearMonth = benefitYearMonth
                entity.outcomeStartDay = outcomeStartDay
                entity.outcomeEndDay = outcomeEndDay
                // not null
                entity.benefitName = plccCardTypeLimit.benefitName ?: ""
                entity.benefitCode = plccCardTypeLimit.benefitCode
                entity.discountAmount = null
                /** discount_rate가 999999999.00가 오는 경우가 있는데,
                 *  총계 데이터가 오는 경우라 DB에 저장하지 않기에 DB 타입을 늘리지 않는다.
                 */
                entity.discountRate = plccCardTypeLimit.discountRate
                // not null
                entity.totalLimitAmount = plccCardTypeLimit.totalLimitAmount ?: BigDecimal("0.0000")
                // not null
                entity.appliedAmount = plccCardTypeLimit.appliedAmount ?: BigDecimal("0.0000")
                // not null
                entity.limitRemainingAmount = plccCardTypeLimit.limitRemainingAmount ?: BigDecimal("0.0000")
                entity.totalLimitCount = plccCardTypeLimit.totalLimitCount
                entity.appliedCount = plccCardTypeLimit.appliedCount
                entity.limitRemainingCount = plccCardTypeLimit.limitRemainingCount
                entity.totalSalesLimitAmount = plccCardTypeLimit.totalSalesLimitAmount
                entity.appliedSaleAmount = plccCardTypeLimit.appliedSalesAmount
                entity.limitRemainingSalesAmount = plccCardTypeLimit.limitRemainingSalesAmount
                entity.serviceType = plccCardTypeLimit.serviceType?.code.toString()
                entity.lastCheckAt = now
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
