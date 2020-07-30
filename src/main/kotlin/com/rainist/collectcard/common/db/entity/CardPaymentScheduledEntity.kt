package com.rainist.collectcard.common.db.entity

import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.Column
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

    @Column(nullable = false)
    var banksaladUserId: Long,

    @Column(nullable = false)
    var cardCompanyId: String,

    @Column(nullable = false)
    var paymentScheduledTransactionNo: Int,

    @Column(nullable = false)
    var cardCompanyCardId: String,

    var cardName: String,

    var cardNumber: String,

    var cardNumberMask: String,

    var businessLicenseNumber: String,

    var storeName: String,

    var storeNumber: String,

    var cardType: String,

    var cardTypeOrigin: String,

    var cardTransactionType: String,

    var cardTransactionTypeOrigin: String,

    var currencyCode: String,

    @Column(nullable = false)
    var isInstallmentPayment: Boolean,

    @Column(nullable = false)
    var installment: Int,

    var installmentRound: Int,

    @Column(nullable = false)
    var netSalesAmount: BigDecimal,

    var serviceChargeAmount: BigDecimal,

    var taxAmount: BigDecimal,

    var paidPoints: BigDecimal,

    var isPointPay: Boolean,

    var discountAmount: BigDecimal,

    var canceledAmount: BigDecimal,

    var approvalNumber: String,

    var approvalDay: String,

    var approvalTime: String,

    var pointsToEarn: BigDecimal,

    var isOverseaUse: Boolean,

    var paymentDay: String,

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
