package com.rainist.collectcard.common.db.entity

import com.rainist.collectcard.common.converter.CardBillTransactionEncryptConverter
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.annotation.Nullable
import javax.persistence.Column
import javax.persistence.Convert
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

    @Column(nullable = false)
    var billedYearMonth: String? = null,

    @Column(nullable = false)
    var banksaladUserId: Long? = null,

    @Column(nullable = false)
    var cardCompanyId: String? = null,

    @Column(nullable = false)
    var billNumber: String? = null,

    @Column(nullable = false)
    var billType: String? = null,

    @Column(nullable = false)
    var cardBillTransactionNo: Int? = null,

    @Column(nullable = false)
    var cardCompanyCardId: String? = null,

    var cardName: String? = null,

    @Convert(converter = CardBillTransactionEncryptConverter::class)
    @Column(nullable = true, name = "cardNumberEncrypted")
    var cardNumber: String? = null,

    @Convert(converter = CardBillTransactionEncryptConverter::class)
    @Column(nullable = true, name = "cardNumberMaskEncrypted")
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

    @Column(nullable = false)
    var isInstallmentPayment: Boolean? = null,

    @Column(nullable = false)
    var installment: Int? = null,

    var installmentRound: Int? = null,

    @Column(nullable = false, precision = 17, scale = 4)
    var netSalesAmount: BigDecimal? = null,

    @Column(precision = 17, scale = 4)
    var serviceChargeAmount: BigDecimal? = null,

    @Column(precision = 17, scale = 4)
    var taxAmount: BigDecimal? = null,

    @Column(precision = 17, scale = 4)
    var paidPoints: BigDecimal? = null,

    var isPointPay: Boolean? = null,

    @Column(precision = 17, scale = 4)
    var discountAmount: BigDecimal? = null,

    @Column(precision = 17, scale = 4)
    var canceledAmount: BigDecimal? = null,

    @Column(nullable = false)
    var approvalNumber: String? = null,

    @Column(nullable = false)
    var approvalDay: String? = null,

    @Column(nullable = false)
    var approvalTime: String? = null,

    @Column(precision = 17, scale = 4)
    var pointsToEarn: BigDecimal? = null,

    @Column(nullable = false)
    var isOverseaUse: Boolean? = null,

    @Column(nullable = false)
    var paymentDay: String? = null,

    var storeCategory: String? = null,

    var storeCategoryOrigin: String? = null,

    var transactionCountry: String? = null,

    var billingRound: Int? = null,

    @Column(precision = 17, scale = 4)
    var paidAmount: BigDecimal? = null,

    @Column(precision = 17, scale = 4)
    var billedAmount: BigDecimal? = null,

    @Column(precision = 17, scale = 4)
    var billedFee: BigDecimal? = null,

    @Column(precision = 17, scale = 4)
    var remainingAmount: BigDecimal? = null,

    var isPaidFull: Boolean? = null,

    @Column(precision = 17, scale = 4)
    var cashbackAmount: BigDecimal? = null,

    @Column(precision = 17, scale = 4)
    var pointsRate: BigDecimal? = null,

    @Column(nullable = false)
    var lastCheckAt: LocalDateTime? = null,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)
