package com.rainist.collectcard.cardbills.util

import com.rainist.collectcard.cardbills.dto.CardBill
import com.rainist.collectcard.cardbills.dto.CardBillTransaction
import com.rainist.collectcard.common.db.entity.CardBillEntity
import com.rainist.collectcard.common.db.entity.CardBillHistoryEntity
import com.rainist.collectcard.common.db.entity.CardBillTransactionEntity
import com.rainist.collectcard.common.db.entity.CardPaymentScheduledEntity
import java.math.BigDecimal
import java.time.LocalDateTime

class CardBillUtil {
    companion object {
        fun makeCardBillEntity(banksaladUserId: Long, organizationId: String, cardBill: CardBill, now: LocalDateTime): CardBillEntity {
            return CardBillEntity().apply {
                this.banksaladUserId = banksaladUserId
                this.cardCompanyId = organizationId
                this.billNumber = cardBill.billNumber
                this.billType = cardBill.billType
                this.cardType = cardBill.cardType?.name ?: ""
                this.lastCheckAt = now
                this.userName = cardBill.userName
                this.userGrade = cardBill.userGrade
                this.userGradeOrigin = cardBill.userGradeOrigin
                this.paymentDay = cardBill.paymentDay ?: ""
                this.billedYearMonth = cardBill.billedYearMonth ?: ""
                this.nextPaymentDay = cardBill.nextPaymentDay
                this.billingAmount = cardBill.billingAmount?.setScale(4) ?: BigDecimal("0.0000").setScale(4)
                this.prepaidAmount = cardBill.prepaidAmount?.setScale(4) ?: BigDecimal("0.0000").setScale(4)
                this.paymentBankId = cardBill.paymentBankId
                this.paymentAccountNumber = cardBill.paymentAccountNumber
                this.totalPoint = cardBill.totalPoints?.toBigDecimal()?.setScale(4)
                this.expiringPoints = cardBill.expiringPoints?.toBigDecimal()?.setScale(4)
            }
        }

        fun makeCardBillTransactionEntity(banksaladUserId: Long, organizationId: String, billedYearMonth: String?, cardBillTransactionNo: Int, cardBillTransaction: CardBillTransaction, now: LocalDateTime): CardBillTransactionEntity {
            return CardBillTransactionEntity().apply {
                this.banksaladUserId = banksaladUserId
                this.billedYearMonth = billedYearMonth ?: ""
                this.cardCompanyId = organizationId
                this.billNumber = cardBillTransaction.billNumber
                this.billType = cardBillTransaction.billType
                this.cardBillTransactionNo = cardBillTransactionNo
                this.cardCompanyCardId = cardBillTransaction.cardCompanyCardId ?: ""
                this.cardName = cardBillTransaction.cardName
                this.cardNumber = cardBillTransaction.cardNumber?.replace("-", "")?.trim()
                this.cardNumberMask = cardBillTransaction.cardNumberMasked?.replace("-", "")?.trim()
                this.businessLicenseNumber = cardBillTransaction.businessLicenseNumber
                this.storeName = cardBillTransaction.storeName
                this.storeNumber = cardBillTransaction.storeNumber
                this.cardType = cardBillTransaction.cardType?.name
                this.cardTypeOrigin = cardBillTransaction.cardTypeOrigin
                this.cardTransactionType = cardBillTransaction.cardTransactionType?.name
                this.cardTransactionTypeOrigin = cardBillTransaction.cardTransactionTypeOrigin
                this.currencyCode = cardBillTransaction.currencyCode
                this.isInstallmentPayment = cardBillTransaction.isInstallmentPayment ?: false
                this.installment = cardBillTransaction.installment ?: 0
                this.installmentRound = cardBillTransaction.installmentRound
                this.netSalesAmount = cardBillTransaction.netSalesAmount?.setScale(4) ?: BigDecimal("0.0000")
                this.serviceChargeAmount = cardBillTransaction.serviceChargeAmount?.setScale(4)
                this.taxAmount = cardBillTransaction.tax?.setScale(4)
                this.paidPoints = cardBillTransaction.paidPoints?.setScale(4)
                this.isPointPay = cardBillTransaction.isPointPay
                this.discountAmount = cardBillTransaction.discountAmount?.setScale(4)
                this.canceledAmount = cardBillTransaction.canceledAmount?.setScale(4)
                this.approvalNumber = cardBillTransaction.approvalNumber ?: ""
                this.approvalDay = cardBillTransaction.approvalDay ?: ""
                this.approvalTime = cardBillTransaction.approvalTime ?: ""
                this.pointsToEarn = cardBillTransaction.pointsToEarn
                this.isOverseaUse = cardBillTransaction.isOverseaUse ?: false
                this.paymentDay = cardBillTransaction.paymentDay ?: ""
                this.storeCategory = cardBillTransaction.storeCategory
                this.storeCategoryOrigin = cardBillTransaction.storeCategoryOrigin
                this.transactionCountry = cardBillTransaction.transactionCountry
                this.billingRound = cardBillTransaction.billingRound
                this.paidAmount = cardBillTransaction.paidAmount?.setScale(4)
                this.billedAmount = cardBillTransaction.billedAmount?.setScale(4)
                this.billedFee = cardBillTransaction.billedFee?.setScale(4)
                this.remainingAmount = cardBillTransaction.remainingAmount?.setScale(4)
                this.isPaidFull = cardBillTransaction.isPaidFull
                this.cashbackAmount = cardBillTransaction.cashback?.setScale(4)
                this.pointsRate = cardBillTransaction.pointsRate?.setScale(4)
                this.lastCheckAt = now
            }
        }

        fun makeCardPaymentScheduledEntity(banksaladUserId: Long, organizationId: String, indexNo: Int, scheduledPayment: CardBillTransaction, now: LocalDateTime): CardPaymentScheduledEntity {
            return CardPaymentScheduledEntity().apply {
                this.banksaladUserId = banksaladUserId
                this.cardCompanyId = organizationId
                this.billNumber = scheduledPayment.billNumber
                this.billType = scheduledPayment.billType
                this.paymentScheduledTransactionNo = indexNo
                this.cardCompanyCardId = scheduledPayment.cardCompanyCardId ?: ""
                this.cardName = scheduledPayment.cardName
                this.cardNumber = scheduledPayment.cardNumber?.replace("-", "")?.trim()
                this.cardNumberMask = scheduledPayment.cardNumberMasked?.replace("-", "")?.trim()
                this.businessLicenseNumber = scheduledPayment.businessLicenseNumber
                this.storeName = scheduledPayment.storeName
                this.storeNumber = scheduledPayment.storeNumber
                this.cardType = scheduledPayment.cardType?.name
                this.cardTypeOrigin = scheduledPayment.cardTypeOrigin
                this.cardTransactionType = scheduledPayment.cardTransactionType?.name
                this.cardTransactionTypeOrigin = scheduledPayment.cardTransactionTypeOrigin
                this.currencyCode = scheduledPayment.currencyCode
                this.isInstallmentPayment = scheduledPayment.isInstallmentPayment ?: false
                this.installment = scheduledPayment.installment ?: 0
                this.installmentRound = scheduledPayment.installmentRound
                this.netSalesAmount = scheduledPayment.netSalesAmount?.setScale(4) ?: BigDecimal("0.0000")
                this.serviceChargeAmount = scheduledPayment.serviceChargeAmount?.setScale(4)
                this.taxAmount = scheduledPayment.tax?.setScale(4)
                this.paidPoints = scheduledPayment.paidPoints?.setScale(4)
                this.isPointPay = scheduledPayment.isPointPay
                this.discountAmount = scheduledPayment.discountAmount?.setScale(4)
                this.canceledAmount = scheduledPayment.canceledAmount?.setScale(4)
                this.approvalNumber = scheduledPayment.approvalNumber ?: ""
                this.approvalDay = scheduledPayment.approvalDay ?: ""
                this.approvalTime = scheduledPayment.approvalTime ?: ""
                this.pointsToEarn = scheduledPayment.pointsToEarn
                this.isOverseaUse = scheduledPayment.isOverseaUse ?: false
                this.paymentDay = scheduledPayment.paymentDay
                this.storeCategory = scheduledPayment.storeCategory
                this.storeCategoryOrigin = scheduledPayment.storeCategoryOrigin
                this.transactionCountry = scheduledPayment.transactionCountry
                this.billingRound = scheduledPayment.billingRound
                this.paidAmount = scheduledPayment.paidAmount?.setScale(4)
                this.billedAmount = scheduledPayment.billedAmount?.setScale(4)
                this.billedFee = scheduledPayment.billedFee?.setScale(4)
                this.remainingAmount = scheduledPayment.remainingAmount?.setScale(4)
                this.isPaidFull = scheduledPayment.isPaidFull
                this.cashbackAmount = scheduledPayment.cashback?.setScale(4)
                this.pointsRate = scheduledPayment.pointsRate
                this.lastCheckAt = now
            }
        }

        fun makeCardBillHistoryEntityFromCardBillHistory(cardBill: CardBillEntity): CardBillHistoryEntity {
            return CardBillHistoryEntity().apply {
                this.cardBillId = cardBill.cardBillId
                this.banksaladUserId = cardBill.banksaladUserId
                this.cardCompanyId = cardBill.cardCompanyId
                this.billNumber = cardBill.billNumber
                this.billType = cardBill.billType
                this.cardType = cardBill.cardType
                this.lastCheckAt = cardBill.lastCheckAt
                this.userName = cardBill.userName
                this.userGrade = cardBill.userGrade
                this.userGradeOrigin = cardBill.userGradeOrigin
                this.paymentDay = cardBill.paymentDay
                this.billedYearMonth = cardBill.billedYearMonth
                this.nextPaymentDay = cardBill.nextPaymentDay
                this.billingAmount = cardBill.billingAmount?.setScale(4)
                this.prepaidAmount = cardBill.prepaidAmount?.setScale(4)
                this.paymentBankId = cardBill.paymentBankId
                this.paymentAccountNumber = cardBill.paymentAccountNumber
                this.totalPoint = cardBill.totalPoint?.setScale(4)
                this.expiringPoints = cardBill.expiringPoints?.setScale(4)
            }
        }
    }
}
