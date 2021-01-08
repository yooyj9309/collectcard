package com.rainist.collectcard.common.db.entity

import com.rainist.collectcard.common.converter.CardTransactionEncryptConverter
import java.math.BigDecimal
import java.time.LocalDateTime
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

@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(name = "card_transaction")
data class CardTransactionEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var cardTransactionId: Long? = null,

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

    @Convert(converter = CardTransactionEncryptConverter::class)
    @Column(nullable = false, name = "card_number_encrypted")
    var cardNumber: String? = null,

    @Convert(converter = CardTransactionEncryptConverter::class)
    @Column(nullable = true, name = "card_number_mask_encrypted")
    var cardNumberMask: String? = null,

    var amount: BigDecimal? = null, // 매출액 ( 거래금액 )

    var canceledAmount: BigDecimal? = null, // 취소금액

    var partialCanceledAmount: BigDecimal? = null, // 부분취소금액

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

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)
