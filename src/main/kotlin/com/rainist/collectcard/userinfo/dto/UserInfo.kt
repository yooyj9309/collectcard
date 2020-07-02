package com.rainist.collectcard.userinfo.dto

import java.math.BigDecimal
import org.springframework.format.annotation.NumberFormat

data class UserInfo(

    var hasRevolving: Boolean? = null, // 리볼빙 여부
    var hasShortTermLoan: Boolean? = null, // 단기대출 여부
    var hasLongTermLoan: Boolean? = null, // 장기대출 여부
    var hasLoan: Boolean? = null, // 대출 여부
    var loanLimit: Limit? = null, // 대출한도
    var cardLoanLimit: Limit? = null, // 카드론 한도
    var onetimePaymentLimit: Limit? = null, // 1회결제 한도 일시불 한도
    var creditCardLimit: Limit? = null, // 신용카드 이용한도
    var cashAdvanceLimit: Limit? = null, // 현금서비스 이용한도
    var overseaLimit: Limit? = null, // 해외 이용한도
    var debitCardLimit: Limit? = null, // 체크카드 한도
    var installmentLimit: Limit? = null // 할부 한도
)

data class Limit(
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    var totalLimitAmount: BigDecimal? = null, // 총한도

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    var balanceLimitAmount: BigDecimal? = null, // 남은 한도

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    var usedAmount: BigDecimal? = null // 사용금액
)
