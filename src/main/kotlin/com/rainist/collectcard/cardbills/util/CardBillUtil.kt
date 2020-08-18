package com.rainist.collectcard.cardbills.util

import com.rainist.collectcard.cardbills.dto.CardBill
import com.rainist.collectcard.cardbills.dto.CardBillTransaction
import com.rainist.collectcard.common.db.entity.CardBillEntity
import com.rainist.collectcard.common.db.entity.CardBillTransactionEntity
import com.rainist.collectcard.common.db.entity.CardPaymentScheduledEntity
import com.rainist.common.util.DateTimeUtil
import java.math.BigDecimal

class CardBillUtil {
    companion object {
        fun makeCardBillEntity(banksaladUserId: Long, organizationId: String, cardBill: CardBill): CardBillEntity {
            return CardBillEntity().apply {
                this.banksaladUserId = banksaladUserId
                this.cardCompanyId = organizationId
                this.billNumber = cardBill.billNumber
                this.billType = cardBill.billType
                this.lastCheckAt = DateTimeUtil.getLocalDateTime()
                this.userName = cardBill.userName
                this.userGrade = cardBill.userGrade
                this.userGradeOrigin = cardBill.userGradeOrigin
                this.paymentDay = cardBill.paymentDay ?: ""
                this.billedYearMonth = cardBill.billedYearMonth ?: ""
                this.nextPaymentDay = cardBill.nextPaymentDay
                this.billingAmount = cardBill.billingAmount ?: BigDecimal("0.0000")
                this.prepaidAmount = cardBill.prepaidAmount ?: BigDecimal("0.0000")
                this.paymentBankId = cardBill.paymentBankId
                this.paymentAccountNumber = cardBill.paymentAccountNumber
                this.totalPoint = cardBill.totalPoints?.toBigDecimal()
                this.expiringPoints = cardBill.expiringPoints?.toBigDecimal()
            }
        }

        fun makeCardBillTransactionEntity(banksaladUserId: Long, organizationId: String, billedYearMonth: String?, cardBillTransactionNo: Int, cardBillTransaction: CardBillTransaction): CardBillTransactionEntity {
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
                this.netSalesAmount = cardBillTransaction.netSalesAmount ?: BigDecimal("0.0000")
                this.serviceChargeAmount = cardBillTransaction.serviceChargeAmount
                this.taxAmount = cardBillTransaction.tax
                this.paidPoints = cardBillTransaction.paidPoints
                this.isPointPay = cardBillTransaction.isPointPay
                this.discountAmount = cardBillTransaction.discountAmount
                this.canceledAmount = cardBillTransaction.canceledAmount
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
                this.paidAmount = cardBillTransaction.paidAmount
                this.billedAmount = cardBillTransaction.billedAmount
                this.billedFee = cardBillTransaction.billedFee
                this.remainingAmount = cardBillTransaction.remainingAmount
                this.isPaidFull = cardBillTransaction.isPaidFull
                this.cashbackAmount = cardBillTransaction.cashback
                this.pointsRate = cardBillTransaction.pointsRate
                this.lastCheckAt = DateTimeUtil.getLocalDateTime()
            }
        }

        fun makeCardPaymentScheduledEntity(banksaladUserId: Long, organizationId: String, indexNo: Int, scheduledPayment: CardBillTransaction): CardPaymentScheduledEntity {
            return CardPaymentScheduledEntity().apply {
                this.banksaladUserId = banksaladUserId
                this.cardCompanyId = organizationId
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
                this.netSalesAmount = scheduledPayment.netSalesAmount ?: BigDecimal("0.0000")
                this.serviceChargeAmount = scheduledPayment.serviceChargeAmount
                this.taxAmount = scheduledPayment.tax
                this.paidPoints = scheduledPayment.paidPoints
                this.isPointPay = scheduledPayment.isPointPay
                this.discountAmount = scheduledPayment.discountAmount
                this.canceledAmount = scheduledPayment.canceledAmount
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
                this.paidAmount = scheduledPayment.paidAmount
                this.billedAmount = scheduledPayment.billedAmount
                this.billedFee = scheduledPayment.billedFee
                this.remainingAmount = scheduledPayment.remainingAmount
                this.isPaidFull = scheduledPayment.isPaidFull
                this.cashbackAmount = scheduledPayment.cashback
                this.pointsRate = scheduledPayment.pointsRate
                this.lastCheckAt = DateTimeUtil.getLocalDateTime()
                this.lastCheckAt = DateTimeUtil.getLocalDateTime()
            }
        }
    }
}
