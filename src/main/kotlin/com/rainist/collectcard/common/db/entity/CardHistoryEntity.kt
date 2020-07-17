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

    var cardName: String? = null,

    var cardBrandName: String? = null,

    var internationalBrandName: String? = null,

    var cardNumber: String? = null,

    var cardNumberMask: String? = null,

    var cardType: String? = null,

    var issuedDate: LocalDateTime? = null,

    var expirationDate: LocalDateTime? = null,

    var cardStatus: String? = null,

    var lastUseDate: LocalDateTime? = null,

    var annualFee: BigDecimal? = null,

    var paymentBankId: String? = null,

    var paymentAccountNumber: String? = null,

    @Column(columnDefinition = "BIT", length = 1)
    var isBusinessCard: Boolean? = false,

    @CreatedDate
    var createdAt: LocalDateTime = DateTimeUtil.utcNowLocalDateTime(),

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null

) {
    constructor(cardEntity: CardEntity) : this() {
        cardId = cardEntity.cardId
        banksaladUserId = cardEntity.banksaladUserId
        cardCompanyId = cardEntity.cardCompanyId
        cardCompanyCardId = cardEntity.cardCompanyCardId
        lastCheckAt = cardEntity.lastCheckAt
        cardOwnerName = cardEntity.cardOwnerName
        cardOwnerType = cardEntity.cardOwnerType
        cardName = cardEntity.cardName
        cardBrandName = cardEntity.cardBrandName
        internationalBrandName = cardEntity.internationalBrandName
        cardNumber = cardEntity.cardNumber
        cardNumberMask = cardEntity.cardNumberMask
        cardType = cardEntity.cardType
        issuedDate = cardEntity.issuedDate
        expirationDate = cardEntity.expirationDate
        cardStatus = cardEntity.cardStatus
        lastUseDate = cardEntity.lastUseDate
        annualFee = cardEntity.annualFee
        paymentBankId = cardEntity.paymentBankId
        paymentAccountNumber = cardEntity.paymentAccountNumber
        isBusinessCard = cardEntity.isBusinessCard
    }
}
