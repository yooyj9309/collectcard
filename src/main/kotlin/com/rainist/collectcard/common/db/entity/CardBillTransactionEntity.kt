package com.rainist.collectcard.common.db.entity

import java.math.BigDecimal
import java.time.LocalDateTime
import javax.annotation.Nullable
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "card_bill_transaction")
data class CardBillTransactionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var cardBillTransactionId: Long,

    var banksaladUserId: Long,

    var cardCompanyId: String,

    var billNumber: String,

    var cardBillTransactionNo: Int,

    var cardCompanyCardId: String,

    @Nullable
    var cardName: String,

    @Nullable
    var cardNumber: String,

    @Nullable
    var cardNumberMask: String,

    @Nullable
    var businessLicenseNumber: String,

    @Nullable
    var storeName: String,

    @Nullable
    var storeNumber: String,

    @Nullable
    var cardType: String,

    @Nullable
    var cardTypeOrigin: String,

    @Nullable
    var cardTransactionType: String,

    @Nullable
    var cardTransactionTypeOrigin: String,

    @Nullable
    var currencyCode: String,

    var isInstallmentPayment: Boolean,

    var installment: Int,

    @Nullable
    var installmentRound: Int,

    var netSalesAmount: BigDecimal,

    @Nullable
    var serviceChargeAmount: BigDecimal,

    @Nullable
    var taxAmount: BigDecimal,

    @Nullable
    var payedPoints: BigDecimal,

    @Nullable
    var isPointPay: Boolean,

    @Nullable
    var discountAmount: BigDecimal,

    @Nullable
    var canceledAmount: BigDecimal,

    var approvalNumber: String,

    var approvalDate: String,

    var approvalTime: String,

    @Nullable
    var pointsToEarn: BigDecimal,

    var isOverseaUse: Boolean,

    var paymentDate: String,

    @Nullable
    var storeCategory: String,

    @Nullable
    var storeCategoryOrigin: String,

    @Nullable
    var transactionCountry: String,

    @Nullable
    var billingRound: Int,

    @Nullable
    var paidAmount: BigDecimal,

    @Nullable
    var billedAmount: BigDecimal,

    @Nullable
    var billedFee: BigDecimal,

    @Nullable
    var remainingAmount: BigDecimal,

    @Nullable
    var isPaidFull: Boolean,

    @Nullable
    var cashbackAmount: BigDecimal,

    @Nullable
    var pointsRate: BigDecimal,

    var lastCheckAt: LocalDateTime,

    @CreatedDate
    var createdAt: LocalDateTime,

    @LastModifiedDate
    var updatedAt: LocalDateTime
)
