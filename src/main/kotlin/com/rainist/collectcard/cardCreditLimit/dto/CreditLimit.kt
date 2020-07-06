package com.rainist.collectcard.cardCreditLimit.dto

import java.math.BigDecimal

data class CreditLimit(
    // (총한도, 전체) 총 한도액
    var totalAmount: BigDecimal,

    // (총한도, 전체) 잔여 한도
    var remainedAmount: BigDecimal,

    // (총한도, 전체) 이용 금액
    var usedAmount: BigDecimal,

    // (1회 결제 한도) 총 한도
    var singleTotalAmount: BigDecimal,

    // (1회 결제 한도) 잔여 한도
    var singleRemainedAmount: BigDecimal,

    // (1회 결제 한도) 이용 금액
    var singleUsedAmount: BigDecimal,

    // (할부) 총 한도
    var installmentTotalAmount: BigDecimal,

    // (할부) 잔여 한도
    var installmentRemainedAmount: BigDecimal,

    // (할부) 이용 금
    var installmentUsedAmount: BigDecimal,

    //  (카드론, 현금서비스) 총 한도
    var loanTotalAmount: BigDecimal,

    // (카드론, 현금서비스) 잔여 한도
    var loanRemainedAmount: BigDecimal,

    // (카드론, 현금서비스) 이용 금액
    var loanUsedAmount: BigDecimal

)
