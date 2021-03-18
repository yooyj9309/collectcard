package com.rainist.collectcard.plcc.common.db.entity

import com.rainist.collectcard.plcc.common.converter.PlccCardEncryptConverter
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

@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "plcc_card_history")
data class PlccCardHistoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var plccCardHistoryId: Long? = null,

    var plccCardId: Long? = null,

    @Column(nullable = false)
    var banksaladUserId: Long? = null,

    @Column(nullable = false)
    var cardCompanyId: String? = null,

    @Column(nullable = false)
    var cardCompanyCardId: String? = null,

    var lastCheckAt: LocalDateTime? = null,

    @Convert(converter = PlccCardEncryptConverter::class)
    @Column(nullable = false, name = "card_owner_name_encrypted")
    var cardOwnerName: String? = null,

    @Column(nullable = false)
    var cardOwnerType: String? = null,

    var cardOwnerTypeOrigin: String? = null,

    var cardName: String? = null,

    var cardBrandName: String? = null,

    var internationalBrandName: String? = null,

    @Convert(converter = PlccCardEncryptConverter::class)
    @Column(nullable = false, name = "card_number_encrypted")
    var cardNumber: String? = null,

    @Convert(converter = PlccCardEncryptConverter::class)
    @Column(nullable = true, name = "card_number_mask_encrypted")
    var cardNumberMask: String? = null,

    var cardType: String? = null,

    var cardTypeOrigin: String? = null,

    var cardApplicationDay: String? = null,

    var issuedDay: String? = null,

    var expirationDay: String? = null,

    var cardStatus: String? = null,

    var cardStatusOrigin: String? = null,

    var lastUseDay: String? = null,

    var lastUseTime: String? = null,

    var annualFee: BigDecimal? = null,

    var paymentBankId: String? = null,

    @Convert(converter = PlccCardEncryptConverter::class)
    @Column(nullable = true, name = "payment_account_number_encrypted")
    var paymentAccountNumber: String? = null,

    @Column(columnDefinition = "BIT", length = 1)
    var isBusinessCard: Boolean? = false,

    @Column(columnDefinition = "TINYINT(1)", length = 1)
    var isTrafficSupported: Boolean? = false,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)
