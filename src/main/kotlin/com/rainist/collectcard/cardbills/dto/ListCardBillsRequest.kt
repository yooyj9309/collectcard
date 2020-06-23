package com.rainist.collectcard.cardbills.dto

data class ListCardBillsRequest(
    var dataHeader: ListCardBillsRequestDataHeader? = null,
    var dataBody: ListCardBillsRequestDataBody? = null
)

data class ListCardBillsRequestDataHeader(
    var empty: Any
)

data class ListCardBillsRequestDataBody(
    var startAt: String? = null, // 조회기간 시작일 YYYYMMDD
    var endAt: String? = null, // 조회기간 종료일 YYYYMMDD
    var nextKey: String? = null
)
