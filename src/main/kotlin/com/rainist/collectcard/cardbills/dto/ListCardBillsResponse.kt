package com.rainist.collectcard.cardbills.dto

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto

data class ListCardBillsResponse(
    var dataHeader: ListCardBillsResponseDataHeader? = null,
    var dataBody: ListCardBillsResponseDataBody? = null
)

data class ListCardBillsResponseDataHeader(
    var resultCode: String?,
    var resultMessage: String?
)

data class ListCardBillsResponseDataBody(
    // 결제 예정 상세 내역
    var cardBills: MutableList<CardBill>,
    // 다음 조회 key
    var nextKey: String?
)

fun ListCardBillsResponse.toListCardBillsResponseProto(): CollectcardProto.ListCardBillsResponse {
    return CollectcardProto.ListCardBillsResponse.newBuilder().build()
}
