package com.rainist.collectcard.plcc.common.db.entity

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

@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(name = "plcc_card_benefit_limit_detail")
data class PlccCardTypeLimitEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var plccCardBenefitLimitDetailId: Long? = null,

    @Column(nullable = false)
    var banksaladUserId: Long? = null,

    @Column(nullable = false)
    var cardCompanyId: String? = null,

    @Column(nullable = false)
    var cardCompanyCardId: String? = null,

    @Column(nullable = false)
    var benefitYearMonth: String? = null,

    var outcomeStartDay: String? = null,

    var outcomeEndDay: String? = null,

    @Column(nullable = false)
    var benefitName: String? = null,

    var benefitCode: String? = null,

    @Column(precision = 17, scale = 4)
    var discountAmount: BigDecimal? = null,

    @Column(precision = 9, scale = 4)
    var discountRate: BigDecimal? = null,

    @Column(nullable = false, precision = 17, scale = 4)
    var totalLimitAmount: BigDecimal? = null,

    @Column(nullable = false, precision = 17, scale = 4)
    var appliedAmount: BigDecimal? = null,

    @Column(nullable = false, precision = 17, scale = 4)
    var limitRemainingAmount: BigDecimal? = null,

    var totalLimitCount: Int? = null,

    var appliedCount: Int? = null,

    var limitRemainingCount: Int? = null,

    @Column(precision = 17, scale = 4)
    var totalSalesLimitAmount: BigDecimal? = null,

    @Column(precision = 17, scale = 4)
    var appliedSaleAmount: BigDecimal? = null,

    @Column(precision = 17, scale = 4)
    var limitRemainingSalesAmount: BigDecimal? = null,

    var serviceType: String? = null,

    @Column(nullable = false)
    var lastCheckAt: LocalDateTime? = null,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
) {
    fun equal(other: PlccCardTypeLimitEntity): Boolean {
        val t = this.copy(plccCardBenefitLimitDetailId = null, lastCheckAt = null, createdAt = null, updatedAt = null)
        val o = other.copy(plccCardBenefitLimitDetailId = null, lastCheckAt = null, createdAt = null, updatedAt = null)
        return t == o
    }
}
