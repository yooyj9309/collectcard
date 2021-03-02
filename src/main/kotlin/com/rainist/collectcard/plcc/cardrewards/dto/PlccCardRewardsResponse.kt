package com.rainist.collectcard.plcc.cardrewards.dto

import java.math.BigDecimal

data class PlccCardRewardsResponse(
    var dataHeader: PlccCardRewardsResponseDataHeader? = null,
    var dataBody: PlccCardRewardsResponseDataBody? = null
)

data class PlccCardRewardsResponseDataHeader(
    var empty: Any? = null
)

data class PlccCardRewardsResponseDataBody(
    var outcomeStartDate: String? = null, // 실적시작일
    var outcomeEndDate: String? = null, // 실적종료일
    var isDelay: Boolean? = null, // 실적유예여부
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
    var benefitListCount: Int? = null, // 혜택실적한도내역건수
    var benefitList: MutableList<PlccCardBenefits>? = null // 혜택실적한도적용내역
)

enum class PromotionCode(
    var index: Int,
    var code: Int,
    var description: String
) {
    NO_PROMOTION(0, 0, "혜택없음"),
    ISSUED(1, 1, "혜택발급"),
    UNKNOWN(99, 3, "알수없음")
}
