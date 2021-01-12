package com.rainist.collectcard.cardcreditlimit.util

import com.rainist.collectcard.cardcreditlimit.dto.CreditLimit
import com.rainist.collectcard.common.db.entity.CreditLimitEntity
import com.rainist.collectcard.common.db.entity.CreditLimitHistoryEntity
import java.math.BigDecimal
import java.time.LocalDateTime

class CreditLimitEntityUtil {
    companion object {

        fun isUpdated(from: CreditLimitEntity, to: CreditLimitEntity): Boolean {
            if (from.onetimePaymentLimitAmount?.compareTo(to.onetimePaymentLimitAmount) != 0) return true
            if (from.onetimePaymentLimitUsedAmount?.compareTo(to.onetimePaymentLimitUsedAmount) != 0) return true
            if (from.onetimePaymentLimitRemainingAmount?.compareTo(to.onetimePaymentLimitRemainingAmount) != 0) return true
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
            if (from.installmentLimitTotalAmount?.compareTo(to.installmentLimitTotalAmount) != 0) return true
            if (from.installmentLimitUsedAmount?.compareTo(to.installmentLimitUsedAmount) != 0) return true
            if (from.installmentLimitRemainingAmount?.compareTo(to.installmentLimitRemainingAmount) != 0) return true

            return false
        }

        fun copyCreditLimitEntity(
            lastCheckAt: LocalDateTime,
            sourceCreditLimitEntity: CreditLimitEntity,
            targetCreditLimitEntity: CreditLimitEntity
        ) {
            targetCreditLimitEntity.apply {
                this.banksaladUserId = sourceCreditLimitEntity.banksaladUserId
                this.cardCompanyId = sourceCreditLimitEntity.cardCompanyId
                this.onetimePaymentLimitAmount = sourceCreditLimitEntity.onetimePaymentLimitAmount
                this.onetimePaymentLimitUsedAmount = sourceCreditLimitEntity.onetimePaymentLimitUsedAmount
                this.onetimePaymentLimitRemainingAmount = sourceCreditLimitEntity.onetimePaymentLimitRemainingAmount
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
                this.installmentLimitTotalAmount = sourceCreditLimitEntity.installmentLimitTotalAmount
                this.installmentLimitUsedAmount = sourceCreditLimitEntity.installmentLimitUsedAmount
                this.installmentLimitRemainingAmount = sourceCreditLimitEntity.installmentLimitRemainingAmount
                this.lastCheckAt = lastCheckAt
            }
        }

        fun makeCreditLimitEntity(
            lastCheckAt: LocalDateTime,
            banksaladUserId: Long?,
            cardCompanyId: String?,
            creditLimit: CreditLimit?
        ): CreditLimitEntity {

            return CreditLimitEntity().apply {
                this.banksaladUserId = banksaladUserId
                this.cardCompanyId = cardCompanyId
                this.onetimePaymentLimitAmount =
                    getNonNullBigDecimal(creditLimit?.onetimePaymentLimit?.totalLimitAmount)
                this.onetimePaymentLimitUsedAmount = getNonNullBigDecimal(creditLimit?.onetimePaymentLimit?.usedAmount)
                this.onetimePaymentLimitRemainingAmount =
                    getNonNullBigDecimal(creditLimit?.onetimePaymentLimit?.remainedAmount)
                this.creditCardLimitTotalAmount = getNonNullBigDecimal(creditLimit?.creditCardLimit?.totalLimitAmount)
                this.creditCardLimitUsedAmount = getNonNullBigDecimal(creditLimit?.creditCardLimit?.usedAmount)
                this.creditCardLimitRemainingAmount = getNonNullBigDecimal(creditLimit?.creditCardLimit?.remainedAmount)
                this.cashAdvanceLimitTotalAmount = getNonNullBigDecimal(creditLimit?.cashServiceLimit?.totalLimitAmount)
                this.cashAdvanceLimitUsedAmount = getNonNullBigDecimal(creditLimit?.cashServiceLimit?.usedAmount)
                this.cashAdvanceLimitRemainingAmount =
                    getNonNullBigDecimal(creditLimit?.cashServiceLimit?.remainedAmount)
                this.overseaLimitTotalAmount = getNonNullBigDecimal(creditLimit?.overseaLimit?.totalLimitAmount)
                this.overseaLimitUsedAmount = getNonNullBigDecimal(creditLimit?.overseaLimit?.usedAmount)
                this.overseaLimitRemainingAmount = getNonNullBigDecimal(creditLimit?.overseaLimit?.remainedAmount)
                this.loanLimitTotalAmount = getNonNullBigDecimal(creditLimit?.loanLimit?.totalLimitAmount)
                this.loanLimitRemainingAmount = getNonNullBigDecimal(creditLimit?.loanLimit?.remainedAmount)
                this.loanLimitUsedAmount = getNonNullBigDecimal(creditLimit?.loanLimit?.usedAmount)
                this.cardLoanLimitTotalAmount = getNonNullBigDecimal(creditLimit?.cardLoanLimit?.totalLimitAmount)
                this.cardLoanLimitUsedAmount = getNonNullBigDecimal(creditLimit?.cardLoanLimit?.usedAmount)
                this.cardLoanLimitRemainingAmount = getNonNullBigDecimal(creditLimit?.cardLoanLimit?.remainedAmount)
                this.debitCardTotalAmount = getNonNullBigDecimal(creditLimit?.debitCardLimit?.totalLimitAmount)
                this.debitCardUsedAmount = getNonNullBigDecimal(creditLimit?.debitCardLimit?.usedAmount)
                this.debitCardRemainingAmount = getNonNullBigDecimal(creditLimit?.debitCardLimit?.remainedAmount)
                this.installmentLimitTotalAmount = getNonNullBigDecimal(creditLimit?.installmentLimit?.totalLimitAmount)
                this.installmentLimitUsedAmount = getNonNullBigDecimal(creditLimit?.installmentLimit?.usedAmount)
                this.installmentLimitRemainingAmount =
                    getNonNullBigDecimal(creditLimit?.installmentLimit?.remainedAmount)
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
                this.onetimePaymentLimitUsedAmount = creditLimitEntity.onetimePaymentLimitUsedAmount
                this.onetimePaymentLimitRemainingAmount = creditLimitEntity.onetimePaymentLimitRemainingAmount
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
                this.installmentLimitTotalAmount = creditLimitEntity.installmentLimitTotalAmount
                this.installmentLimitUsedAmount = creditLimitEntity.installmentLimitUsedAmount
                this.installmentLimitRemainingAmount = creditLimitEntity.installmentLimitRemainingAmount
            }
        }

        private fun getNonNullBigDecimal(value: BigDecimal?): BigDecimal {
            return value?.setScale(4) ?: BigDecimal(0).setScale(4)
        }
    }
}
