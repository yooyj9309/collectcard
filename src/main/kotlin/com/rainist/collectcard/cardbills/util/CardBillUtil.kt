package com.rainist.collectcard.cardbills.util

import com.rainist.collectcard.cardbills.dto.CardBill
import com.rainist.collectcard.cardbills.dto.CardBillTransaction
import com.rainist.collectcard.common.db.entity.CardBillEntity
import com.rainist.collectcard.common.db.entity.CardBillHistoryEntity
import com.rainist.collectcard.common.db.entity.CardBillScheduledEntity
import com.rainist.collectcard.common.db.entity.CardBillScheduledHistoryEntity
import com.rainist.collectcard.common.db.entity.CardBillTransactionEntity
import com.rainist.collectcard.common.db.entity.CardBillTransactionHistoryEntity
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
                this.pointsToEarn = cardBillTransaction.pointsToEarn?.setScale(4)
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
                this.pointsToEarn = scheduledPayment.pointsToEarn?.setScale(4)
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
                this.pointsRate = scheduledPayment.pointsRate?.setScale(4)
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

        fun makeCardBillTransactionHistoryEntity(entity: CardBillTransactionEntity): CardBillTransactionHistoryEntity {
            return CardBillTransactionHistoryEntity().apply {
                this.cardBillTransactionId = entity.cardBillTransactionId
                this.billedYearMonth = entity.billedYearMonth
                this.banksaladUserId = entity.banksaladUserId
                this.cardCompanyId = entity.cardCompanyId
                this.billNumber = entity.billNumber
                this.billType = entity.billType
                this.cardBillTransactionNo = entity.cardBillTransactionNo
                this.cardCompanyCardId = entity.cardCompanyCardId
                this.cardName = entity.cardName
                this.cardNumber = entity.cardNumber
                this.cardNumberMask = entity.cardNumberMask
                this.businessLicenseNumber = entity.businessLicenseNumber
                this.storeName = entity.storeName
                this.storeNumber = entity.storeNumber
                this.cardType = entity.cardType
                this.cardTypeOrigin = entity.cardTypeOrigin
                this.cardTransactionType = entity.cardTransactionType
                this.cardTransactionTypeOrigin = entity.cardTransactionTypeOrigin
                this.currencyCode = entity.currencyCode
                this.isInstallmentPayment = entity.isInstallmentPayment
                this.installment = entity.installment
                this.installmentRound = entity.installmentRound
                this.netSalesAmount = entity.netSalesAmount
                this.serviceChargeAmount = entity.serviceChargeAmount
                this.taxAmount = entity.taxAmount
                this.paidPoints = entity.paidPoints
                this.isPointPay = entity.isPointPay
                this.discountAmount = entity.discountAmount
                this.canceledAmount = entity.canceledAmount
                this.approvalNumber = entity.approvalNumber
                this.approvalDay = entity.approvalDay
                this.approvalTime = entity.approvalTime
                this.pointsToEarn = entity.pointsToEarn
                this.isOverseaUse = entity.isOverseaUse
                this.paymentDay = entity.paymentDay
                this.storeCategory = entity.storeCategory
                this.storeCategoryOrigin = entity.storeCategoryOrigin
                this.transactionCountry = entity.transactionCountry
                this.billingRound = entity.billingRound
                this.paidAmount = entity.paidAmount
                this.billedAmount = entity.billedAmount
                this.billedFee = entity.billedFee
                this.remainingAmount = entity.remainingAmount
                this.isPaidFull = entity.isPaidFull
                this.cashbackAmount = entity.cashbackAmount
                this.pointsRate = entity.pointsRate
                this.lastCheckAt = entity.lastCheckAt
            }
        }

        fun makeCardBillScheduledEntity(banksaladUserId: Long, organizationId: String, cardBill: CardBill, now: LocalDateTime): CardBillScheduledEntity {
            return CardBillScheduledEntity().apply {
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

        fun makeCardBillScheduledHistoryEntityFromCardBillScheduledEntity(cardScheduledBill: CardBillScheduledEntity): CardBillScheduledHistoryEntity {
            return CardBillScheduledHistoryEntity().apply {
                this.cardBillScheduledId = cardScheduledBill.cardBillScheduledId
                this.banksaladUserId = cardScheduledBill.banksaladUserId
                this.cardCompanyId = cardScheduledBill.cardCompanyId
                this.billNumber = cardScheduledBill.billNumber
                this.billType = cardScheduledBill.billType
                this.cardType = cardScheduledBill.cardType
                this.lastCheckAt = cardScheduledBill.lastCheckAt
                this.userName = cardScheduledBill.userName
                this.userGrade = cardScheduledBill.userGrade
                this.userGradeOrigin = cardScheduledBill.userGradeOrigin
                this.paymentDay = cardScheduledBill.paymentDay
                this.billedYearMonth = cardScheduledBill.billedYearMonth
                this.nextPaymentDay = cardScheduledBill.nextPaymentDay
                this.billingAmount = cardScheduledBill.billingAmount?.setScale(4)
                this.prepaidAmount = cardScheduledBill.prepaidAmount?.setScale(4)
                this.paymentBankId = cardScheduledBill.paymentBankId
                this.paymentAccountNumber = cardScheduledBill.paymentAccountNumber
                this.totalPoint = cardScheduledBill.totalPoint?.setScale(4)
                this.expiringPoints = cardScheduledBill.expiringPoints?.setScale(4)
            }
        }
    }
}
