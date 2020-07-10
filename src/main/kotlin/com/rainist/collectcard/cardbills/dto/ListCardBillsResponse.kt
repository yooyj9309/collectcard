package com.rainist.collectcard.cardbills.dto

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto

data class ListCardBillsResponse(
    var dataHeader: ListCardBillsResponseDataHeader? = null,
    var dataBody: ListCardBillsResponseDataBody? = null
)

data class ListCardBillsResponseDataHeader(
    var resultCode: String?,
    var resultMessage: String?,
    var successCode: String?
)

data class ListCardBillsResponseDataBody(
    // 결제 예정 상세 내역
    var cardBills: MutableList<CardBill>? = null,
    // 다음 조회 key
    var nextKey: String = ""
)

fun ListCardBillsResponse.toListCardBillsResponseProto(): CollectcardProto.ListCardBillsResponse {
    // TODO 응답값 받는 부분 추가.
    return CollectcardProto.ListCardBillsResponse.newBuilder().build()
}
