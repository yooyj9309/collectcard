package com.rainist.collectcard.common.db.entity

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
@Table(name = "card_history")
data class CardHistoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var cardHistoryId: Long? = null,
    var cardId: Long? = null,
    var banksaladUserId: Long? = null,
    var cardCompanyId: String? = null,
    var cardCompanyCardId: String? = null,
    var lastCheckAt: LocalDateTime? = null,
    var cardOwnerName: String? = null,
    var cardOwnerType: String? = null,
    var cardOwnerTypeOrigin: String? = null,
    var cardName: String? = null,
    var cardBrandName: String? = null,
    var internationalBrandName: String? = null,
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
    var createdAt: LocalDateTime = DateTimeUtil.utcNowLocalDateTime(),
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)

fun CardHistoryEntity.makeCardHistoryEntity(cardEntity: CardEntity): CardHistoryEntity {
    this.cardId = cardEntity.cardId
    this.banksaladUserId = cardEntity.banksaladUserId
    this.cardCompanyId = cardEntity.cardCompanyId
    this.cardCompanyCardId = cardEntity.cardCompanyCardId
    this.lastCheckAt = cardEntity.lastCheckAt
    this.cardOwnerName = cardEntity.cardOwnerName
    this.cardOwnerType = cardEntity.cardOwnerType
    this.cardOwnerTypeOrigin = cardEntity.cardOwnerTypeOrigin
    this.cardName = cardEntity.cardName
    this.cardBrandName = cardEntity.cardBrandName
    this.internationalBrandName = cardEntity.internationalBrandName
    this.cardNumber = cardEntity.cardNumber
    this.cardNumberMask = cardEntity.cardNumberMask
    this.cardType = cardEntity.cardType
    this.cardTypeOrigin = cardEntity.cardTypeOrigin
    this.issuedDay = cardEntity.issuedDay
    this.expirationDay = cardEntity.expirationDay
    this.cardStatus = cardEntity.cardStatus
    this.cardStatusOrigin = cardEntity.cardStatusOrigin
    this.lastUseDay = cardEntity.lastUseDay
    this.lastUseTime = cardEntity.lastUseTime
    this.annualFee = cardEntity.annualFee
    this.paymentBankId = cardEntity.paymentBankId
    this.paymentAccountNumber = cardEntity.paymentAccountNumber
    this.isBusinessCard = cardEntity.isBusinessCard
    return this
}
