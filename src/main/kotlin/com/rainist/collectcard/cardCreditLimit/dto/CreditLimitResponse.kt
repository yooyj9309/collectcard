package com.rainist.collectcard.cardCreditLimit.dto

data class CreditLimitResponse(
    var dataHeader: CreditLimitResponseDataHeader?,
    var dataBody: CreditLimitResponseDataBody?
)

data class CreditLimitResponseDataHeader(
    var resultCode: String? = null,
    var resultMessage: String? = null
)

data class CreditLimitResponseDataBody(
    var creditlimitInfo: CreditLimit? = null
)
