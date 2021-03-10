package com.rainist.collectcard.plcc.cardrewards.dto

data class PlccCardRewardsResponse(
    var dataHeader: PlccCardRewardsResponseDataHeader? = null,
    var dataBody: PlccCardRewardsResponseDataBody? = null
)

data class PlccCardRewardsResponseDataHeader(
    var empty: Any? = null
)

data class PlccCardRewardsResponseDataBody(
    var plccCardPlccCardRewardsThreshold: PlccCardRewardsThreshold? = null,
    var benefitList: MutableList<PlccCardRewardsTypeLimit>? = null // 혜택실적한도적용내역
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
