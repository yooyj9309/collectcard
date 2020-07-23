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
@Table(name = "card")
data class CardEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
