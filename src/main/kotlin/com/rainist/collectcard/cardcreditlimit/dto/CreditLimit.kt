package com.rainist.collectcard.cardcreditlimit.dto

import com.rainist.collectcard.common.db.entity.CreditLimitEntity
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

    fun allDataIsNull(): Boolean {
        return (totalLimitAmount == null) && (remainedAmount == null) && (usedAmount == null)
    }

    // 엔티티의 필드를 DTO의 속성에 맞춰서 넣는다.
    companion object {
        // 대출 한도
        fun toLoanLimit(creditLimitEntity: CreditLimitEntity): Limit {
            return Limit().apply {
                this.totalLimitAmount = creditLimitEntity.loanLimitTotalAmount
                this.remainedAmount = creditLimitEntity.loanLimitRemainingAmount
                this.usedAmount = creditLimitEntity.loanLimitUsedAmount
            }
        }

        // 일회결제한도
        fun toOnetimePaymentLimit(creditLimitEntity: CreditLimitEntity): Limit {
            return Limit().apply {
                this.totalLimitAmount = creditLimitEntity.onetimePaymentLimitAmount
                this.remainedAmount = creditLimitEntity.onetimePaymentLimitRemainingAmount
                this.usedAmount = creditLimitEntity.onetimePaymentLimitUsedAmount
            }
        }

        // 카드론 한도
        fun toCardLoanLimit(creditLimitEntity: CreditLimitEntity): Limit {
            return Limit().apply {
                this.totalLimitAmount = creditLimitEntity.cardLoanLimitTotalAmount
                this.remainedAmount = creditLimitEntity.cardLoanLimitRemainingAmount
                this.usedAmount = creditLimitEntity.cardLoanLimitUsedAmount
            }
        }

        // 신용카드 한도
        fun toCreditCardLimit(creditLimitEntity: CreditLimitEntity): Limit {
            return Limit().apply {
                this.totalLimitAmount = creditLimitEntity.creditCardLimitTotalAmount
                this.remainedAmount = creditLimitEntity.creditCardLimitRemainingAmount
                this.usedAmount = creditLimitEntity.creditCardLimitUsedAmount
            }
        }

        // 직불카드 한도
        fun toDebitCardLimit(creditLimitEntity: CreditLimitEntity): Limit {
            return Limit().apply {
                this.totalLimitAmount = creditLimitEntity.debitCardTotalAmount
                this.remainedAmount = creditLimitEntity.debitCardRemainingAmount
                this.usedAmount = creditLimitEntity.debitCardUsedAmount
            }
        }

        // 현금서비스 한도
        fun toCashServiceLimit(creditLimitEntity: CreditLimitEntity): Limit {
            return Limit().apply {
                this.totalLimitAmount = creditLimitEntity.cashAdvanceLimitTotalAmount
                this.remainedAmount = creditLimitEntity.cashAdvanceLimitRemainingAmount
                this.usedAmount = creditLimitEntity.cashAdvanceLimitUsedAmount
            }
        }

        // 해외 한도
        fun toOverseaLimit(creditLimitEntity: CreditLimitEntity): Limit {
            return Limit().apply {
                this.totalLimitAmount = creditLimitEntity.overseaLimitTotalAmount
                this.remainedAmount = creditLimitEntity.overseaLimitRemainingAmount
                this.usedAmount = creditLimitEntity.overseaLimitUsedAmount
            }
        }

        // 할부 한도
        fun toInstallmentLimit(creditLimitEntity: CreditLimitEntity): Limit {
            return Limit().apply {
                this.totalLimitAmount = creditLimitEntity.installmentLimitTotalAmount
                this.remainedAmount = creditLimitEntity.installmentLimitRemainingAmount
                this.usedAmount = creditLimitEntity.installmentLimitUsedAmount
            }
        }
    }
}
