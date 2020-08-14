package com.rainist.collectcard.cardtransactions.dto

import com.rainist.common.annotation.validation.StringDateFormat
import javax.validation.constraints.NotNull

data class ListTransactionsRequest(
    var dataHeader: ListTransactionsRequestDataHeader? = null,
    var dataBody: ListTransactionsRequestDataBody? = null,
    var organizationObjectid: String? = null
)

data class ListTransactionsRequestDataHeader(
    var empty: Any? = null
)

data class ListTransactionsRequestDataBody(

    // 조회기간 시작일 YYYYMMDD default now() prev 12month
    @field:StringDateFormat(pattern = "yyyyMMdd")
    var startAt: String = "",

    // 조회기간 종료일 YYYYMMDD default now()
    @field:StringDateFormat(pattern = "yyyyMMdd")
    var endAt: String = "",

    var cardId: String? = null, // 카드 구분자 ( 카드번호, 암호화된 카드번호 , CID 등등 )

    var tag: String? = null, // Custom tag ( 신한 : bc tag )

    var cardOwnerType: String? = "0", // 카드 소유주 타입 ( 본인 , 가족 ) , CONNECT_CARD 기준 NONE :0, 본인 : 1, 가족 : 2

    @field:NotNull
    var nextKey: String = "" // pagination 방식 중 nextKey
)
