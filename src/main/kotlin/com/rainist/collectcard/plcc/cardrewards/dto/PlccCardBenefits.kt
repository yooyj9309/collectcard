package com.rainist.collectcard.plcc.cardrewards.dto

import java.math.BigDecimal

data class PlccCardBenefits(
    var benefitName: String? = null, // 혜택유형명
    var benefitCode: String? = null, // 혜택코드
    var discountRate: BigDecimal? = null, // 할인율
    var totalLimitAmount: BigDecimal? = null, // 총한도금액
    var appliedAmount: BigDecimal? = null, // 적용금액
    var remainingLimitAmount: BigDecimal? = null, // 잔여한도금액
    var totalLimitCount: Int? = null, // 총한도횟수
    var appliedCount: Int? = null, // 적용횟수
    var remainingLimitCount: Int? = null, // 잔여한도횟수
    var totalSalesLimitAmount: BigDecimal? = null, // 총한도매출금액
    var appliedSalesAmount: BigDecimal? = null, // 적용매출금액
    var remainingLimitSalesAmount: BigDecimal? = null, // 잔여한도매출금액
    var serviceType: ServiceType? = null // 서비스유형
)

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
