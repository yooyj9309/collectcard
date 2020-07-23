package com.rainist.collectcard.common.db.entity

import java.math.BigDecimal
import java.time.LocalDateTime
import javax.annotation.Nullable
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

    var banksaladUserId: Long? = null,

    var cardCompanyId: String? = null,

    var billNumber: String? = null,

    var lastCheckAt: LocalDateTime? = null,

    @Nullable
    var userName: String? = null,

    @Nullable
    var userGrade: String? = null,

    @Nullable
    var userGradeOrigin: String? = null,

    var paymentDay: String? = null,

    var billedYearMonth: String? = null,

    var nextPaymentDay: String? = null,

    var billingAmount: BigDecimal? = null,

    var prepaidAmount: BigDecimal? = null,

    @Nullable
    var paymentBankId: String? = null,

    @Nullable
    var paymentAccountNumber: String? = null,

    @Nullable
    var totalPoint: BigDecimal? = null,

    @Nullable
    var expiringPoints: BigDecimal? = null,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)
