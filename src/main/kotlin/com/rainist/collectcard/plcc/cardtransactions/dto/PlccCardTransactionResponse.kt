package com.rainist.collectcard.plcc.cardtransactions.dto

import java.math.BigDecimal

data class PlccCardTransactionResponse(
    var dataHeader: PlccCardTransactionResponseDataHeader? = null,
    var dataBody: PlccCardTransactionResponseDataBody? = null
)

data class PlccCardTransactionResponseDataHeader(
    var empty: Any? = null // 없는 경우 빈값
)

data class PlccCardTransactionResponseDataBody(
    var totalBenefitCount: Int? = null, // 총 혜택 건수
    var totalBenefitAmount: BigDecimal? = null, // 총 혜택 금액
    var totalSalesAmount: BigDecimal? = null, // 총 혜택 매출 금액
    var responseCode: String? = null, // 응답코드
    var transactionListCount: Int = 0, // 상품별할인서비스 적용내역 건수
    var transactionList: List<PlccCardTransaction> = listOf() // 상품별 할인 서비스 적용 내역
)
