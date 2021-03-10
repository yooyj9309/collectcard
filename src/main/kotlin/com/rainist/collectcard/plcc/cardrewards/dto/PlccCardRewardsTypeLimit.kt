package com.rainist.collectcard.plcc.cardrewards.dto

import com.rainist.collectcard.plcc.common.db.entity.PlccCardTypeLimitEntity
import java.math.BigDecimal
import java.time.LocalDateTime

data class PlccCardRewardsTypeLimit(
    var benefitName: String? = null, // 혜택유형명
    var benefitCode: String? = null, // 혜택코드
    var discountRate: BigDecimal? = null, // 할인율
    var totalLimitAmount: BigDecimal? = null, // 총한도금액
    var appliedAmount: BigDecimal? = null, // 적용금액
    var limitRemainingAmount: BigDecimal? = null, // 잔여한도금액
    var totalLimitCount: Int? = null, // 총한도횟수
    var appliedCount: Int? = null, // 적용횟수
    var limitRemainingCount: Int? = null, // 잔여한도횟수
    var totalSalesLimitAmount: BigDecimal? = null, // 총한도매출금액
    var appliedSalesAmount: BigDecimal? = null, // 적용매출금액
    var limitRemainingSalesAmount: BigDecimal? = null, // 잔여한도매출금액
    var serviceType: ServiceType? = null // 서비스유형
) {
    fun toEntity(
        banksaladUserId: Long,
        cardCompanyId: String,
        cardCompanyCardId: String,
        benefitYearMonth: String?,
        outcomeStartDay: String,
        outcomeEndDay: String,
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
            entity.benefitName = benefitName ?: ""
            entity.benefitCode = benefitCode
            entity.discountAmount = null
            entity.discountRate = discountRate
            // not null
            entity.totalLimitAmount = totalLimitAmount ?: BigDecimal("0.0000")
            // not null
            entity.appliedAmount = appliedAmount ?: BigDecimal("0.0000")
            // not null
            entity.limitRemainingAmount = limitRemainingAmount ?: BigDecimal("0.0000")
            entity.totalLimitCount = totalLimitCount
            entity.appliedCount = appliedCount
            entity.limitRemainingCount = limitRemainingCount
            entity.totalSalesLimitAmount = totalSalesLimitAmount
            entity.appliedSaleAmount = appliedSalesAmount
            entity.limitRemainingSalesAmount = limitRemainingSalesAmount
            entity.serviceType = serviceType?.code.toString()
            entity.lastCheckAt = now
        }
    }
}

enum class ServiceType(
    var index: Int,
    var code: Int,
    var description: String
) {
    CHARGE_DISCOUNT(0, 0, "청구할인"),
    POINT(1, 1, "포인트"),
    INSTALLMENT_REDUCT(2, 2, "할부감면"),
    UNKNOWN(3, 3, "알수없음")
}
