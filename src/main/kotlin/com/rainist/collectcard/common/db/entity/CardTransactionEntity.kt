package com.rainist.collectcard.common.db.entity

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
@Table(name = "card_transaction")
data class CardTransactionEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var cardTransactionId: Long? = null,

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

    @Column(nullable = false)
    var cardNumber: String? = null,

    var cardNumberMask: String? = null,

    var businessLicenseNumber: String? = null,

    var storeName: String? = null,

    var storeNumber: String? = null,

    var cardType: String? = null,

    var cardTypeOrigin: String? = null,

    @Column(nullable = false)
    var cardTransactionType: String? = null,

    var cardTransactionTypeOrigin: String? = null,

    var currencyCode: String? = null,

    @Column(nullable = false, columnDefinition = "BIT", length = 1)
    var isInstallmentPayment: Boolean? = false,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)
