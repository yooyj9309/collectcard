package com.rainist.collectcard.plcc.cardrewards.dto

data class PlccCardRewardsRequest(
    var dataHeader: PlccCardRewardsRequestDataHeader? = null,
    var dataBody: PlccCardRewardsRequestDataBody? = null
)

data class PlccCardRewardsRequestDataHeader(
    var empty: Any? = null
)

data class PlccCardRewardsRequestDataBody(
    var inquiryYearMonth: String? = null, // 조회년월
    var cardNumber: String? = null // 카드번호
)
