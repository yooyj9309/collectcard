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
    var cardBillTransactionId: Long? = null,

    var banksaladUserId: Long? = null,

    var cardCompanyId: String? = null,

    var billNumber: String? = null,

    var cardBillTransactionNo: Int? = null,

    var cardCompanyCardId: String? = null,

    @Nullable
    var cardName: String? = null,

    @Nullable
    var cardNumber: String? = null,

    @Nullable
    var cardNumberMask: String? = null,

    @Nullable
    var businessLicenseNumber: String? = null,

    @Nullable
    var storeName: String? = null,

    @Nullable
    var storeNumber: String? = null,

    @Nullable
    var cardType: String? = null,

    @Nullable
    var cardTypeOrigin: String? = null,

    @Nullable
    var cardTransactionType: String? = null,

    @Nullable
    var cardTransactionTypeOrigin: String? = null,

    @Nullable
    var currencyCode: String? = null,

    var isInstallmentPayment: Boolean? = null,

    var installment: Int? = null,

    @Nullable
    var installmentRound: Int? = null,

    var netSalesAmount: BigDecimal? = null,

    @Nullable
    var serviceChargeAmount: BigDecimal? = null,

    @Nullable
    var taxAmount: BigDecimal? = null,

    @Nullable
    var paidPoints: BigDecimal? = null,

    @Nullable
    var isPointPay: Boolean? = null,

    @Nullable
    var discountAmount: BigDecimal? = null,

    @Nullable
    var canceledAmount: BigDecimal? = null,

    var approvalNumber: String? = null,

    var approvalDay: String? = null,

    var approvalTime: String? = null,

    @Nullable
    var pointsToEarn: BigDecimal? = null,

    var isOverseaUse: Boolean? = null,

    var paymentDay: String? = null,

    @Nullable
    var storeCategory: String? = null,

    @Nullable
    var storeCategoryOrigin: String? = null,

    @Nullable
    var transactionCountry: String? = null,

    @Nullable
    var billingRound: Int? = null,

    @Nullable
    var paidAmount: BigDecimal? = null,

    @Nullable
    var billedAmount: BigDecimal? = null,

    @Nullable
    var billedFee: BigDecimal? = null,

    @Nullable
    var remainingAmount: BigDecimal? = null,

    @Nullable
    var isPaidFull: Boolean? = null,

    @Nullable
    var cashbackAmount: BigDecimal? = null,

    @Nullable
    var pointsRate: BigDecimal? = null,

    var lastCheckAt: LocalDateTime? = null,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)
