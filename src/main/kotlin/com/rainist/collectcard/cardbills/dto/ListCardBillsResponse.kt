package com.rainist.collectcard.cardbills.dto

data class ListCardBillsResponse(
    var dataHeader: ListCardBillsResponseDataHeader,
    var dataBody: ListCardBillsResponseDataBody
)

data class ListCardBillsResponseDataHeader(
    var resultCode: String?,
    var resultMessage: String?
)

data class ListCardBillsResponseDataBody(
    var cardBills: List<CardBill>
)
