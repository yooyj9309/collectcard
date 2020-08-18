package com.rainist.collectcard.common.db.entity

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
@Table(name = "card_bill")
data class CardBillEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var cardBillId: Long? = null,

    @Column(nullable = false)
    var banksaladUserId: Long? = null,

    @Column(nullable = false)
    var cardCompanyId: String? = null,

    @Column(nullable = false)
    var billNumber: String? = null,

    @Column(nullable = false)
    var billType: String? = null,

    @Column(nullable = false)
    var lastCheckAt: LocalDateTime? = null,

    var userName: String? = null,

    var userGrade: String? = null,

    var userGradeOrigin: String? = null,

    @Column(nullable = false)
    var paymentDay: String? = null,

    @Column(nullable = false)
    var billedYearMonth: String? = null,

    var nextPaymentDay: String? = null,

    @Column(nullable = false)
    var billingAmount: BigDecimal? = null,

    @Column(nullable = false)
    var prepaidAmount: BigDecimal? = null,

    var paymentBankId: String? = null,

    var paymentAccountNumber: String? = null,

    var totalPoint: BigDecimal? = null,

    var expiringPoints: BigDecimal? = null,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
) {
    fun equal(other: CardBillEntity): Boolean {
        val t = this.copy(cardBillId = null, lastCheckAt = null, createdAt = null, updatedAt = null)
        val o = other.copy(cardBillId = null, lastCheckAt = null, createdAt = null, updatedAt = null)
        return t == o
    }
}
