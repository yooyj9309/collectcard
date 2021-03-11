package com.rainist.collectcard.plcc.cardrewards.dto

data class PlccCardRewardsResponse(
    var dataHeader: PlccCardRewardsResponseDataHeader? = null,
    var dataBody: PlccCardRewardsResponseDataBody? = null
)

data class PlccCardRewardsResponseDataHeader(
    var empty: Any? = null
)

data class PlccCardRewardsResponseDataBody(
    var plccCardThreshold: PlccCardThreshold? = null,
    var benefitList: List<PlccCardTypeLimit> = listOf() // 혜택실적한도적용내역
)
