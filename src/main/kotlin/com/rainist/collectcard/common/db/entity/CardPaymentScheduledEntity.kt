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
@Table(name = "card_payment_scheduled")
data class CardPaymentScheduledEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var cardPaymentScheduledId: Long,

    var banksaladUserId: Long,

    var cardCompanyId: String,

    var paymentScheduledTransactionNo: Int,

    @Nullable
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

    var cardTypeOrigin: String,

    @Nullable
    var cardTransactionType: String,

    var cardTransactionTypeOrigin: String,

    var currencyCode: String,

    var isInstallmentPayment: Boolean,

    var installment: Int,

    var installmentRound: Int,

    var netSalesAmount: BigDecimal,

    var serviceChargeAmount: BigDecimal,

    var taxAmount: BigDecimal,

    var payedPoints: BigDecimal,

    var isPointPay: Boolean,

    var discountAmount: BigDecimal,

    var canceledAmount: BigDecimal,

    var approvalNumber: String,

    var approvalDate: String,

    var approvalTime: String,

    var pointsToEarn: BigDecimal,

    var isOverseaUse: Boolean,

    var paymentDate: String,

    var storeCategory: String,

    var storeCategoryOrigin: String,

    var transactionCountry: String,

    var billingRound: Int,

    var paidAmount: BigDecimal,

    var billedAmount: BigDecimal,

    var billedFee: BigDecimal,

    var remainingAmount: BigDecimal,

    var isPaidFull: Boolean,

    var cashbackAmount: BigDecimal,

    var pointsRate: BigDecimal,

    var lastCheckAt: LocalDateTime,

    @CreatedDate
    var createdAt: LocalDateTime,

    @LastModifiedDate
    var updatedAt: LocalDateTime
)
