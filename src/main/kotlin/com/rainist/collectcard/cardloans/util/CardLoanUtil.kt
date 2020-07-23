package com.rainist.collectcard.cardloans.util

import com.rainist.collectcard.cardloans.dto.Loan
import com.rainist.collectcard.common.db.entity.CardLoanEntity
import com.rainist.collectcard.common.db.entity.CardLoanHistoryEntity
import java.math.BigDecimal
import java.time.LocalDateTime

class CardLoanUtil {

    companion object {
        fun makeCardLoanEntity(banksaladUserId: String, organizationId: String?, loan: Loan): CardLoanEntity {
            return CardLoanEntity().apply {
                this.banksaladUserId = banksaladUserId.toLong()
                this.cardCompanyId = organizationId
                this.cardCompanyLoanId = loan.loanId
                this.loanName = loan.loanName
                this.paymentBankId = loan.paymentBankId
                this.expirationDay = loan.expirationDay
                this.loanStatus = loan.loanStatus.name
                this.loanStatusOrigin = loan.loanStatusOrigin
                this.paymentAccountNumber = loan.paymentAccountNumber
                this.repaymentMethod = loan.repaymentMethod.name
                this.repaymentMethodOrigin = loan.repaymentMethodOrigin
                this.withdrawalDay = loan.withdrawalDay
                this.interestRate = loan.interestRate ?: BigDecimal(0) // default 0
                this.loanCategory = loan.loanCategory
                this.currencyCode = "KRW" // todo 해당부분이 loan에 없음 확인필요
                this.additionalLoanAmount = loan.additionalLoanAmount ?: BigDecimal(0) // default 0
                this.fullyPaidDay = loan.fullyPaidDay
                this.cardNumber = loan.cardNumber
                this.principalAmount = loan.principalAmount ?: BigDecimal(0) // default 0
                this.interestAmount = loan.interestAmount ?: BigDecimal(0) // default 0
                this.loanNumber = loan.loanNumber
                this.loanAmount = loan.loanAmount ?: BigDecimal(0) // default 0
                this.loanRemainingAmount = loan.remainingAmount ?: BigDecimal(0) // default 0
            }
        }

        fun makeCardLoanHistoryEntity(lastCheckAt: LocalDateTime, cardLoanEntity: CardLoanEntity): CardLoanHistoryEntity {
            return CardLoanHistoryEntity().apply {
                this.cardLoanId = cardLoanEntity.cardLoanId
                this.banksaladUserId = cardLoanEntity.banksaladUserId
                this.cardCompanyId = cardLoanEntity.cardCompanyId
                this.cardCompanyLoanId = cardLoanEntity.cardCompanyLoanId
                this.lastCheckAt = lastCheckAt
                this.loanName = cardLoanEntity.loanName
                this.paymentBankId = cardLoanEntity.paymentBankId
                this.expirationDay = cardLoanEntity.expirationDay
                this.loanStatus = cardLoanEntity.loanStatus
                this.loanStatusOrigin = cardLoanEntity.loanStatusOrigin
                this.paymentAccountNumber = cardLoanEntity.paymentAccountNumber
                this.repaymentMethod = cardLoanEntity.repaymentMethod
                this.repaymentMethodOrigin = cardLoanEntity.repaymentMethodOrigin
                this.withdrawalDay = cardLoanEntity.withdrawalDay
                this.interestRate = cardLoanEntity.interestRate
                this.loanCategory = cardLoanEntity.loanCategory
                this.currencyCode = cardLoanEntity.currencyCode
                this.additionalLoanAmount = cardLoanEntity.additionalLoanAmount
                this.fullyPaidDay = cardLoanEntity.fullyPaidDay
                this.cardNumber = cardLoanEntity.cardNumber
                this.principalAmount = cardLoanEntity.principalAmount
                this.interestAmount = cardLoanEntity.interestAmount
                this.loanNumber = cardLoanEntity.loanNumber
                this.loanAmount = cardLoanEntity.loanAmount
                this.loanRemainingAmount = cardLoanEntity.loanRemainingAmount
            }
        }

        fun isUpdated(from: CardLoanEntity, to: CardLoanEntity): Boolean {
            if (from.expirationDay != to.expirationDay) return true
            if (from.loanStatus != to.loanStatus) return true
            if (from.repaymentMethod != to.repaymentMethod) return true
            if (from.withdrawalDay != to.withdrawalDay) return true
            if (from.loanCategory != to.loanCategory) return true
            if (from.cardNumber != to.cardNumber) return true
            if (from.loanNumber != to.loanNumber) return true
            if (from.fullyPaidDay != to.fullyPaidDay) return true
            if (from.additionalLoanAmount?.compareTo(to.additionalLoanAmount) != 0) return true
            if (from.interestRate?.compareTo(to.interestRate) != 0) return true
            if (from.principalAmount?.compareTo(to.principalAmount) != 0) return true
            if (from.interestAmount?.compareTo(to.interestAmount) != 0) return true
            if (from.loanAmount?.compareTo(to.loanAmount) != 0) return true
            if (from.loanRemainingAmount?.compareTo(to.loanRemainingAmount) != 0) return true

            return false
        }

        fun copyCardLoanEntity(sourceEntity: CardLoanEntity, targetEntity: CardLoanEntity) {
            targetEntity.expirationDay = sourceEntity.expirationDay
            targetEntity.loanStatus = sourceEntity.loanStatus
            targetEntity.loanStatusOrigin = sourceEntity.loanStatusOrigin
            targetEntity.repaymentMethod = sourceEntity.repaymentMethod
            targetEntity.repaymentMethodOrigin = sourceEntity.repaymentMethodOrigin
            targetEntity.withdrawalDay = sourceEntity.withdrawalDay
            targetEntity.interestRate = sourceEntity.interestRate
            targetEntity.loanCategory = sourceEntity.loanCategory
            targetEntity.additionalLoanAmount = sourceEntity.additionalLoanAmount
            targetEntity.fullyPaidDay = sourceEntity.fullyPaidDay
            targetEntity.cardNumber = sourceEntity.cardNumber
            targetEntity.principalAmount = sourceEntity.principalAmount
            targetEntity.interestAmount = sourceEntity.interestAmount
            targetEntity.loanNumber = sourceEntity.loanNumber
            targetEntity.loanAmount = sourceEntity.loanAmount
            targetEntity.loanRemainingAmount = sourceEntity.loanRemainingAmount
        }
    }
}
