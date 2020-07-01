package com.rainist.collectcard.cardtransactions.dto

import com.rainist.collectcard.cardtransactions.CardTransactionServiceImpl
import com.rainist.common.annotation.validation.StringDateFormat
import com.rainist.common.util.DateTimeUtil
import javax.validation.constraints.NotEmpty

data class ListTransactionsRequest(
    var dataHeader: ListTransactionsRequestDataHeader? = null,
    var dataBody: ListTransactionsRequestDataBody? = null
)

data class ListTransactionsRequestDataHeader(
    var empty: Any? = null
)

data class ListTransactionsRequestDataBody(

    // 조회기간 시작일 YYYYMMDD default now() prev 12month
    @field:StringDateFormat(pattern = "yyyyMMdd")
    var startAt: String = DateTimeUtil.kstNowLocalDate().minusMonths(CardTransactionServiceImpl.MAX_MONTH).let { DateTimeUtil.localDateToString(it, "yyyyMMdd") },

    // 조회기간 종료일 YYYYMMDD default now()
    @field:StringDateFormat(pattern = "yyyyMMdd")
    var endAt: String = DateTimeUtil.kstNowLocalDateString("yyyyMMdd"),

    var cardId: String? = null, // 카드 구분자 ( 카드번호, 암호화된 카드번호 , CID 등등 )

    var tag: String? = null, // Custom tag ( 신한 : bc tag )

    var cardOwnerType: String? = null, // 카드 소유주 타입 ( 본인 , 가족 )

    @field:NotEmpty
    var nextKey: String? = "" // pagination 방식 중 nextKey
)
