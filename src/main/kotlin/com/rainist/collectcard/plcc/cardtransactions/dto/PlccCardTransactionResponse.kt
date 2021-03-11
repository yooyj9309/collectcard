package com.rainist.collectcard.plcc.cardtransactions.dto

import com.rainist.collectcard.common.enums.ResultCode
import java.math.BigDecimal

data class PlccCardTransactionResponse(
    var dataHeader: PlccCardTransactionResponseDataHeader? = null,
    var dataBody: PlccCardTransactionResponseDataBody? = null
)

data class PlccCardTransactionResponseDataHeader(
    var empty: Any? = null // 없는 경우 빈값
)

data class PlccCardTransactionResponseDataBody(
    var totalBenefitCount: Int = 0, // 총 혜택 건수
    var totalBenefitAmount: BigDecimal = BigDecimal.ZERO, // 총 혜택 금액
    var totalSalesAmount: BigDecimal = BigDecimal.ZERO, // 총 혜택 매출 금액
    var responseCode: ResultCode = ResultCode.UNKNOWN, // 응답코드
    var responseMessage: String? = null,
    var transactionListCount: Int = 0, // 상품별할인서비스 적용내역 건수
    var transactionList: List<PlccCardTransaction> = listOf() // 상품별 할인 서비스 적용 내역
)
