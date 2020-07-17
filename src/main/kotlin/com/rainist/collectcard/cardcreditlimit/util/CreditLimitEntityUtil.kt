package com.rainist.collectcard.cardcreditlimit.util

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimit
import com.rainist.collectcard.cardcreditlimit.entity.CreditLimitEntity
import com.rainist.collectcard.cardcreditlimit.entity.CreditLimitHistoryEntity
import java.math.BigDecimal
import java.time.LocalDateTime

class CreditLimitEntityUtil {
    companion object {

        fun diffCheck(from: CreditLimitEntity, to: CreditLimitEntity): Boolean {
            if (from.onetimePaymentLimitAmount?.compareTo(to.onetimePaymentLimitAmount) != 0) return true
            if (from.onetimePaymentLimitAmount?.compareTo(to.onetimePaymentLimitAmount) != 0) return true
            if (from.creditCardLimitTotalAmount?.compareTo(to.creditCardLimitTotalAmount) != 0) return true
            if (from.creditCardLimitUsedAmount?.compareTo(to.creditCardLimitUsedAmount) != 0) return true
            if (from.creditCardLimitRemainingAmount?.compareTo(to.creditCardLimitRemainingAmount) != 0) return true
            if (from.cashAdvanceLimitTotalAmount?.compareTo(to.cashAdvanceLimitTotalAmount) != 0) return true
            if (from.cashAdvanceLimitUsedAmount?.compareTo(to.cashAdvanceLimitUsedAmount) != 0) return true
            if (from.cashAdvanceLimitRemainingAmount?.compareTo(to.cashAdvanceLimitRemainingAmount) != 0) return true
            if (from.overseaLimitTotalAmount?.compareTo(to.overseaLimitTotalAmount) != 0) return true
            if (from.overseaLimitUsedAmount?.compareTo(to.overseaLimitUsedAmount) != 0) return true
            if (from.overseaLimitRemainingAmount?.compareTo(to.overseaLimitRemainingAmount) != 0) return true
            if (from.loanLimitTotalAmount?.compareTo(to.loanLimitTotalAmount) != 0) return true
            if (from.loanLimitRemainingAmount?.compareTo(to.loanLimitRemainingAmount) != 0) return true
            if (from.loanLimitUsedAmount?.compareTo(to.loanLimitUsedAmount) != 0) return true
            if (from.cardLoanLimitTotalAmount?.compareTo(to.cardLoanLimitTotalAmount) != 0) return true
            if (from.cardLoanLimitUsedAmount?.compareTo(to.cardLoanLimitUsedAmount) != 0) return true
            if (from.cardLoanLimitRemainingAmount?.compareTo(to.cardLoanLimitRemainingAmount) != 0) return true
            if (from.debitCardTotalAmount?.compareTo(to.debitCardTotalAmount) != 0) return true
            if (from.debitCardUsedAmount?.compareTo(to.debitCardUsedAmount) != 0) return true
            if (from.debitCardRemainingAmount?.compareTo(to.debitCardRemainingAmount) != 0) return true

            return false
        }

        fun copyCreditLimitEntity(lastCheckAt: LocalDateTime, sourceCreditLimitEntity: CreditLimitEntity, targetCreditLimitEntity: CreditLimitEntity) {
            targetCreditLimitEntity.apply {
                this.banksaladUserId = sourceCreditLimitEntity.banksaladUserId
                this.cardCompanyId = sourceCreditLimitEntity.cardCompanyId
                this.onetimePaymentLimitAmount = sourceCreditLimitEntity.onetimePaymentLimitAmount
                this.creditCardLimitTotalAmount = sourceCreditLimitEntity.creditCardLimitTotalAmount
                this.creditCardLimitTotalAmount = sourceCreditLimitEntity.creditCardLimitTotalAmount
                this.creditCardLimitUsedAmount = sourceCreditLimitEntity.creditCardLimitUsedAmount
                this.creditCardLimitRemainingAmount = sourceCreditLimitEntity.creditCardLimitRemainingAmount
                this.cashAdvanceLimitTotalAmount = sourceCreditLimitEntity.cashAdvanceLimitTotalAmount
                this.cashAdvanceLimitUsedAmount = sourceCreditLimitEntity.cashAdvanceLimitUsedAmount
                this.cashAdvanceLimitRemainingAmount = sourceCreditLimitEntity.cashAdvanceLimitRemainingAmount
                this.overseaLimitTotalAmount = sourceCreditLimitEntity.overseaLimitTotalAmount
                this.overseaLimitUsedAmount = sourceCreditLimitEntity.overseaLimitUsedAmount
                this.overseaLimitRemainingAmount = sourceCreditLimitEntity.overseaLimitRemainingAmount
                this.loanLimitTotalAmount = sourceCreditLimitEntity.loanLimitTotalAmount
                this.loanLimitRemainingAmount = sourceCreditLimitEntity.loanLimitRemainingAmount
                this.loanLimitUsedAmount = sourceCreditLimitEntity.loanLimitUsedAmount
                this.cardLoanLimitTotalAmount = sourceCreditLimitEntity.cardLoanLimitTotalAmount
                this.cardLoanLimitUsedAmount = sourceCreditLimitEntity.cardLoanLimitUsedAmount
                this.cardLoanLimitRemainingAmount = sourceCreditLimitEntity.cardLoanLimitRemainingAmount
                this.debitCardTotalAmount = sourceCreditLimitEntity.debitCardTotalAmount
                this.debitCardUsedAmount = sourceCreditLimitEntity.debitCardUsedAmount
                this.debitCardRemainingAmount = sourceCreditLimitEntity.debitCardRemainingAmount
                this.lastCheckAt = lastCheckAt
            }
        }

        fun makeCreditLimitEntity(lastCheckAt: LocalDateTime, request: CollectcardProto.GetCreditLimitRequest, creditLimit: CreditLimit): CreditLimitEntity {
            // TODO 각 카드사별로 없는 값이 있을 경우 어떤 값을 추가할지에 대한 논의 필요 -> 우선 -1 값 적용
            return CreditLimitEntity().apply {
                this.banksaladUserId = request.userId.toLong()
                this.cardCompanyId = request.companyId.value
                this.onetimePaymentLimitAmount = creditLimit.onetimePaymentLimit?.totalLimitAmount ?: BigDecimal(-1)
                this.creditCardLimitTotalAmount = creditLimit.creditCardLimit?.totalLimitAmount ?: BigDecimal(-1)
                this.creditCardLimitUsedAmount = creditLimit.creditCardLimit?.usedAmount ?: BigDecimal(-1)
                this.creditCardLimitRemainingAmount = creditLimit.creditCardLimit?.remainedAmount ?: BigDecimal(-1)
                this.cashAdvanceLimitTotalAmount = creditLimit.cashServiceLimit?.totalLimitAmount ?: BigDecimal(-1)
                this.cashAdvanceLimitUsedAmount = creditLimit.cashServiceLimit?.usedAmount ?: BigDecimal(-1)
                this.cashAdvanceLimitRemainingAmount = creditLimit.cashServiceLimit?.remainedAmount ?: BigDecimal(-1)
                this.overseaLimitTotalAmount = creditLimit.overseaLimit?.totalLimitAmount ?: BigDecimal(-1)
                this.overseaLimitUsedAmount = creditLimit.overseaLimit?.usedAmount ?: BigDecimal(-1)
                this.overseaLimitRemainingAmount = creditLimit.overseaLimit?.remainedAmount ?: BigDecimal(-1)
                this.loanLimitTotalAmount = creditLimit.loanLimit?.totalLimitAmount ?: BigDecimal(-1)
                this.loanLimitRemainingAmount = creditLimit.loanLimit?.usedAmount ?: BigDecimal(-1)
                this.loanLimitUsedAmount = creditLimit.loanLimit?.remainedAmount ?: BigDecimal(-1)
                this.cardLoanLimitTotalAmount = creditLimit.cardLoanLimit?.totalLimitAmount ?: BigDecimal(-1)
                this.cardLoanLimitUsedAmount = creditLimit.cardLoanLimit?.usedAmount ?: BigDecimal(-1)
                this.cardLoanLimitRemainingAmount = creditLimit.cardLoanLimit?.remainedAmount ?: BigDecimal(-1)
                this.debitCardTotalAmount = creditLimit.debitCardLimit?.totalLimitAmount ?: BigDecimal(-1)
                this.debitCardUsedAmount = creditLimit.debitCardLimit?.usedAmount ?: BigDecimal(-1)
                this.debitCardRemainingAmount = creditLimit.debitCardLimit?.remainedAmount ?: BigDecimal(-1)
                this.lastCheckAt = lastCheckAt
            }
        }

        fun makeCreditLimitHistoryEntity(creditLimitEntity: CreditLimitEntity): CreditLimitHistoryEntity {
            return CreditLimitHistoryEntity().apply {
                this.banksaladUserId = creditLimitEntity.banksaladUserId
                this.cardLimitId = creditLimitEntity.cardLimitId
                this.cardCompanyId = creditLimitEntity.cardCompanyId
                this.lastCheckAt = creditLimitEntity.lastCheckAt
                this.onetimePaymentLimitAmount = creditLimitEntity.onetimePaymentLimitAmount
                this.creditCardLimitTotalAmount = creditLimitEntity.creditCardLimitTotalAmount
                this.creditCardLimitUsedAmount = creditLimitEntity.creditCardLimitUsedAmount
                this.creditCardLimitRemainingAmount = creditLimitEntity.creditCardLimitRemainingAmount
                this.cashAdvanceLimitTotalAmount = creditLimitEntity.cashAdvanceLimitTotalAmount
                this.cashAdvanceLimitUsedAmount = creditLimitEntity.cashAdvanceLimitUsedAmount
                this.cashAdvanceLimitRemainingAmount = creditLimitEntity.cashAdvanceLimitRemainingAmount
                this.overseaLimitTotalAmount = creditLimitEntity.overseaLimitTotalAmount
                this.overseaLimitUsedAmount = creditLimitEntity.overseaLimitUsedAmount
                this.overseaLimitRemainingAmount = creditLimitEntity.overseaLimitRemainingAmount
                this.loanLimitTotalAmount = creditLimitEntity.loanLimitTotalAmount
                this.loanLimitRemainingAmount = creditLimitEntity.loanLimitRemainingAmount
                this.loanLimitUsedAmount = creditLimitEntity.loanLimitUsedAmount
                this.cardLoanLimitTotalAmount = creditLimitEntity.cardLoanLimitTotalAmount
                this.cardLoanLimitUsedAmount = creditLimitEntity.cardLoanLimitUsedAmount
                this.cardLoanLimitRemainingAmount = creditLimitEntity.cardLoanLimitRemainingAmount
                this.debitCardTotalAmount = creditLimitEntity.debitCardTotalAmount
                this.debitCardUsedAmount = creditLimitEntity.debitCardUsedAmount
                this.debitCardRemainingAmount = creditLimitEntity.debitCardRemainingAmount
            }
        }
    }
}
