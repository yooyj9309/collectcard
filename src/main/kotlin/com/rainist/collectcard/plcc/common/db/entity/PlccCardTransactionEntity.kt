package com.rainist.collectcard.plcc.common.db.entity

import com.rainist.collectcard.common.enums.CardOwnerType
import com.rainist.collectcard.common.enums.CardTransactionType
import com.rainist.collectcard.common.enums.CardType
import com.rainist.collectcard.plcc.cardtransactions.enums.PlccCardServiceType
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.EnumType
import javax.persistence.Enumerated
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

    @Column(nullable = true, name = "card_number_encrypted")
    var cardNumber: String? = null,

    @Column(nullable = true, name = "card_number_mask_encrypted")
    var cardNumberMask: String? = null,

    var amount: BigDecimal? = null,

    var canceledAmount: BigDecimal? = null,

    @Column(nullable = false)
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

    @Enumerated(EnumType.STRING)
    var cardType: CardType? = null,

    var cardTypeOrigin: String? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var cardTransactionType: CardTransactionType? = null,

    var cardTransactionTypeOrigin: String? = null,

    var currencyCode: String? = null,

    var transactionCountry: String? = null,

    @Column(nullable = false, columnDefinition = "BIT", length = 1)
    var isInstallmentPayment: Boolean? = false,

    @Column(nullable = false)
    var installment: Int? = null,

    var paymentDay: String? = null,

    @Column(nullable = false, columnDefinition = "BIT", length = 1)
    var isOverseaUse: Boolean? = false,

    var benefitCode: String? = null,

    var benefitCodeOrigin: String? = null,

    @Column(nullable = false)
    var benefitName: String? = null,

    @Enumerated(EnumType.STRING)
    var serviceType: PlccCardServiceType? = null,

    var serviceTypeOrigin: String? = null,

    @Enumerated(EnumType.STRING)
    var cardOwnerType: CardOwnerType? = null,

    var cardOwnerTypeOrigin: String? = null,

    var lastCheckAt: LocalDateTime? = null,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)
