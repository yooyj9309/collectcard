package com.rainist.collectcard.plcc.cardtransactions.dto

data class PlccCardTransactionRequest(
    var dataHeader: PlccCardTransactionRequestDataHeader? = null,
    var dataBody: PlccCardTransactionRequestDataBody? = null
)

data class PlccCardTransactionRequestDataHeader(
    var empty: Any? = null // 없는 경우 빈값
)

data class PlccCardTransactionRequestDataBody(
    var inquiryYearMonth: String? = null, // 조회년월
    var cardNumber: String? = null, // 카드번호
    var productCode: String? = null, // 상품코드
    var inquiryCode: String? = null // 조회코드
)
