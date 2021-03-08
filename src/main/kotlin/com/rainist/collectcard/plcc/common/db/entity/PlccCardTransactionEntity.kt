package com.rainist.collectcard.plcc.common.db.entity

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

@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(name = "plcc_card_transaction")
data class PlccCardTransactionEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var plccCardTransactionId: Long? = null,

    @Column(nullable = false)
    var approvalYearMonth: String? = "",

    @Column(nullable = false)
    var banksaladUserId: Long? = null,

    @Column(nullable = false)
    var cardCompanyId: String? = null,

    @Column(nullable = false)
    var cardCompanyCardId: String? = null,

    @Column(nullable = false)
    var approvalNumber: String? = null,

    var approvalDay: String? = null,

    var approvalTime: String? = null,

    var cardName: String? = null,

    @Column(nullable = false, name = "card_number_encrypted")
    var cardNumber: String? = null,

    @Column(nullable = true, name = "card_number_mask_encrypted")
    var cardNumberMask: String? = null,

    var amount: BigDecimal? = null,

    var canceledAmount: BigDecimal? = null,

    var discountAmount: BigDecimal? = null,

    var discountRate: BigDecimal? = null,

    var partialCanceledAmount: BigDecimal? = null,

    var tax: BigDecimal? = null, // 부가세

    var serviceChargeAmount: BigDecimal? = null, // 봉사료

    var netSalesAmount: BigDecimal? = null, // 순매출액

    var businessLicenseNumber: String? = null,

    var storeName: String? = null,

    var storeNumber: String? = null,

    var storeCategory: String? = null,

    var cardType: String? = null,

    var cardTypeOrigin: String? = null,

    @Column(nullable = false)
    var cardTransactionType: String? = null,

    var cardTransactionTypeOrigin: String? = null,

    var currencyCode: String? = null,

    var transactionCountry: String? = null,

    @Column(nullable = false, columnDefinition = "BIT", length = 1)
    var isInstallmentPayment: Boolean? = false,

    var installment: Int? = null,

    var paymentDay: String? = null,

    @Column(nullable = false, columnDefinition = "BIT", length = 1)
    var isOverseaUse: Boolean? = false,

    var benefitCode: String? = null,

    var benefitName: String? = null,

    var benefitType: String? = null,

    var lastCheckAt: LocalDateTime? = null,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)
