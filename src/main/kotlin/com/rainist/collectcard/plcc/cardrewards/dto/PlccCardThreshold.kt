package com.rainist.collectcard.plcc.cardrewards.dto

import java.math.BigDecimal

data class PlccCardThreshold(
    var outcomeStartDate: String? = null, // 실적시작일
    var outcomeEndDate: String? = null, // 실적종료일
    var isOutcomeDelay: Boolean? = null, // 실적유예여부
    var beforeMonthCriteriaUseAmount: BigDecimal? = null, // 전월실적기준이용금액
    var outcomeCriteriaAmount: BigDecimal? = null, // 실적기준금액
    var responseCode: String? = null, // 응답코드
    var responseMessage: String? = null // 응답메세지
)
