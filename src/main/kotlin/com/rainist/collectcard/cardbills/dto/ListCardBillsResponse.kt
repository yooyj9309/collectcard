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
    // 결제 예정 상세 내역
    var cardBills: List<CardBill>,
    // 다음 조회 key
    var nextKey: String?
)
