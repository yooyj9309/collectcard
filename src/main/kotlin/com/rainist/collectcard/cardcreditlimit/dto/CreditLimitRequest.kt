package com.rainist.collectcard.cardcreditlimit.dto

data class CreditLimitRequest(
    var dataHeader: CardCreditLimitRequestDataHeader? = null,
    var dataBody: CardCreditLimitRequestDataBody? = null
)

data class CardCreditLimitRequestDataHeader(
    var empty: Any? = null
)

data class CardCreditLimitRequestDataBody(
    var empty: Any? = null
)
