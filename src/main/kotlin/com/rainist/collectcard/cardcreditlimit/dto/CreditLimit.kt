package com.rainist.collectcard.cardcreditlimit.dto

import java.math.BigDecimal
import org.springframework.format.annotation.NumberFormat

data class CreditLimit(

    // 대출 한도 여부
    var loanLimit: Limit? = null,

    // 일회 결제 한도 여부
    var onetimePaymentLimit: Limit? = null,

    // 카드론 한도 여부
    var cardLoanLimit: Limit? = null,

    // 신용카드 한도 여부
    var creditCardLimit: Limit? = null,

    // 체크카드 한도 여부
    var debitCardLimit: Limit? = null,

    // 현금서비스 한도 여부
    var cashServiceLimit: Limit? = null,

    // 해외 한도 여부
    var overseaLimit: Limit? = null,

    // 할부 한도
    @Deprecated("신규 Proto에는 없는 내역으로 사라질 예정입니다.")
    var installmentLimit: Limit? = null
)

data class Limit(
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    var totalLimitAmount: BigDecimal? = null, // 총한도

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    var remainedAmount: BigDecimal? = null, // 남은 한도

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    var usedAmount: BigDecimal? = null // 사용금액
) {
    fun isAllZeroAmountOrNull(): Boolean {
        return BigDecimal.ZERO == (totalLimitAmount ?: BigDecimal.ZERO) &&
            BigDecimal.ZERO == (remainedAmount ?: BigDecimal.ZERO) &&
            BigDecimal.ZERO == (usedAmount ?: BigDecimal.ZERO)
    }
}
