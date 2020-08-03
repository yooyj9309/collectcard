package com.rainist.collectcard.common.db.entity

import com.rainist.collectcard.card.dto.Card
import com.rainist.common.util.DateTimeUtil
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

    var lastCheckAt: LocalDateTime? = null,

    @Column(nullable = false)
    var cardOwnerName: String? = null,

    @Column(nullable = false)
    var cardOwnerType: String? = null,

    var cardOwnerTypeOrigin: String? = null,

    var cardName: String? = null,

    var cardBrandName: String? = null,

    var internationalBrandName: String? = null,

    @Column(nullable = false)
    var cardNumber: String? = null,

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

    var paymentAccountNumber: String? = null,

    @Column(columnDefinition = "BIT", length = 1)
    var isBusinessCard: Boolean? = false,

    @CreatedDate
    var createdAt: LocalDateTime? = DateTimeUtil.utcNowLocalDateTime(),

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)

fun CardEntity.makeCardEntity(banksaladUserId: Long?, card: Card): CardEntity {
    this.banksaladUserId = banksaladUserId
    this.cardCompanyId = card.cardCompanyId
    this.cardCompanyCardId = card.cardCompanyCardId
    this.lastCheckAt = DateTimeUtil.getLocalDateTime()
    this.cardOwnerName = card.cardOwnerName
    this.cardOwnerType = card.cardOwnerType?.name
    this.cardOwnerTypeOrigin = card.cardOwnerTypeOrigin
    this.cardName = card.cardName
    this.cardBrandName = card.cardBrandName
    this.internationalBrandName = card.internationalBrandName
    this.cardNumber = card.cardNumber
    this.cardNumberMask = card.cardNumberMask
    this.cardType = card.cardType.name
    this.cardTypeOrigin = card.cardTypeOrigin
    this.issuedDay = card.issuedDay
    this.expirationDay = card.expiresDay
    this.cardStatus = card.cardStatus?.name
    this.cardStatusOrigin = card.cardStatusOrigin
    this.lastUseDay = card.lastUseDay
    this.lastUseTime = card.lastUseTime
    this.annualFee = card.annualFee
    this.paymentBankId = card.paymentBankId
    this.paymentAccountNumber = card.paymentAccountNumber
    this.isBusinessCard = card.isBusinessCard
    return this
}
