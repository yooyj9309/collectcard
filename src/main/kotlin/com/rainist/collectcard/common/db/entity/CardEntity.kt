package com.rainist.collectcard.common.db.entity

import com.rainist.collectcard.common.converter.CardEncryptConverter
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
@Table(name = "card")
data class CardEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var cardId: Long? = null,

    @Column(nullable = false)
    var banksaladUserId: Long? = null,

    @Column(nullable = false)
    var cardCompanyId: String? = null,

    @Column(nullable = false)
    var cardCompanyCardId: String? = null,

    // 카드사 카드 아이디 원본 TODO 2020.11.26일 이후 컬럼 삭제
    @Column(nullable = true)
    var cardCompanyCardIdOrigin: String? = null,

    var lastCheckAt: LocalDateTime? = null,

    @Convert(converter = CardEncryptConverter::class)
    @Column(nullable = false, name = "cardOwnerNameEncrypted")
    var cardOwnerName: String? = null,

    @Column(nullable = false)
    var cardOwnerType: String? = null,

    var cardOwnerTypeOrigin: String? = null,

    var cardName: String? = null,

    var cardBrandName: String? = null,

    var internationalBrandName: String? = null,

    @Convert(converter = CardEncryptConverter::class)
    @Column(nullable = false, name = "cardNumberEncrypted")
    var cardNumber: String? = null,

    @Convert(converter = CardEncryptConverter::class)
    @Column(nullable = true, name = "cardNumberMaskEncrypted")
    var cardNumberMask: String? = null,

    var cardType: String? = null,

    var cardTypeOrigin: String? = null,

    var issuedDay: String? = null,

    var expirationDay: String? = null,

    var cardStatus: String? = null,

    var cardStatusOrigin: String? = null,

    var lastUseDay: String? = null,

    var lastUseTime: String? = null,

    var annualFee: BigDecimal? = null,

    var paymentBankId: String? = null,

    @Convert(converter = CardEncryptConverter::class)
    @Column(nullable = true, name = "paymentAccountNumberEncrypted")
    var paymentAccountNumber: String? = null,

    @Column(columnDefinition = "BIT", length = 1)
    var isBusinessCard: Boolean? = false,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)
