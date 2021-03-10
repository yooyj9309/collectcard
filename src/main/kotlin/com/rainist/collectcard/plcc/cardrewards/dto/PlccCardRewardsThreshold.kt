package com.rainist.collectcard.plcc.cardrewards.dto

import com.rainist.collectcard.plcc.common.db.entity.PlccCardThresholdEntity
import java.math.BigDecimal
import java.time.LocalDateTime

data class PlccCardRewardsThreshold(
    var outcomeStartDate: String? = null, // 실적시작일
    var outcomeEndDate: String? = null, // 실적종료일
    var isOutcomeDelay: Boolean? = null, // 실적유예여부
    var beforeMonthCriteriaUseAmount: BigDecimal? = null, // 전월실적기준이용금액
    var outcomeCriteriaAmount: BigDecimal? = null, // 실적기준금액
    var totalBenefitAmount: BigDecimal? = null, // 총적용금액
    var totalBenefitCount: Int? = null, // 총적용건수
    var totalSalesAmount: BigDecimal? = null, // 총매출금액
    var monthlyBenefitRate: BigDecimal? = null, // 월혜택율
    var monthlyBenefitLimit: BigDecimal? = null, // 월혜택한도
    var cashbackAmount: BigDecimal? = null, // 캐시백지급금액
    var message: String? = null, // 메세지내용
    var promotionCode: PromotionCode? = null, // 프로모션 코드
    var responseCode: String? = null, // 응답코드
    var benefitListCount: Int? = null // 혜택실적한도내역건수
) {
    fun toEntity(
        banksaladUserId: Long,
        organizationId: String,
        cardId: String,
        inquiryYearMonth: String?,
        now: LocalDateTime
    ): PlccCardThresholdEntity {
        return PlccCardThresholdEntity().also { entity ->
            entity.banksaladUserId = banksaladUserId
            entity.cardCompanyId = organizationId
            entity.cardCompanyCardId = cardId
            entity.benefitYearMonth = inquiryYearMonth
            entity.outcomeStartDay = outcomeStartDate ?: ""
            entity.outcomeEndDay = outcomeEndDate ?: ""
            entity.isOutcomeDelay = isOutcomeDelay
            // not null
            entity.beforeMonthCriteriaUseAmount = beforeMonthCriteriaUseAmount ?: BigDecimal("0.0000")
            entity.outcomeCriteriaAmount = outcomeCriteriaAmount
            // not null
            entity.totalBenefitAmount = totalBenefitAmount ?: BigDecimal("0.0000")
            entity.totalBenefitCount = totalBenefitCount
            entity.totalSalesAmount = totalSalesAmount
            entity.monthlyBenefitRate = monthlyBenefitRate
            entity.monthlyBenefitAmount = null
            // not null
            entity.monthlyBenefitLimit = monthlyBenefitLimit ?: BigDecimal("0.0000")
            entity.cashbackAmount = cashbackAmount
            entity.benefitMessage = message
            entity.promotionCode = promotionCode?.name.toString()
            entity.lastCheckAt = now
        }
    }

    enum class PromotionCode(
        var index: Int,
        var code: Int,
        var description: String
    ) {
        NO_PROMOTION(0, 0, "혜택없음"),
        ISSUED(1, 1, "혜택발급"),
        UNKNOWN(99, 3, "알수없음")
    }
}
