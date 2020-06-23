package com.rainist.collectcard.cardtransactions.dto

data class ListTransactionsRequest(
    var dataHeader: ListTransactionsRequestDataHeader? = null,
    var dataBody: ListTransactionsRequestDataBody? = null
)

data class ListTransactionsRequestDataHeader(
    var empty: Any? = null
)

data class ListTransactionsRequestDataBody(
    var startAt: String? = null, // 조회기간 시작일 YYYYMMDD
    var endAt: String? = null, // 조회기간 종료일 YYYYMMDD
    var cardId: String? = null, // 카드 구분자 ( 카드번호, 암호화된 카드번호 , CID 등등 )
    var tag: String? = null, // Custom tag ( 신한 : bc tag )
    var nextKey: String? = null, // pagination 방식 중 nextKey
    var cardOwnerType: String? = null // 카드 소유주 타입 ( 본인 , 가족 )
)
