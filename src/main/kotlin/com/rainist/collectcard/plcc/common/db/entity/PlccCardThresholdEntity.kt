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
@Table(name = "plcc_card_benefit_limit")
data class PlccCardThresholdEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var plccCardBenefitLimitId: Long? = null,

    @Column(nullable = false)
    var banksaladUserId: Long? = null,

    @Column(nullable = false)
    var cardCompanyId: String? = null,

    @Column(nullable = false)
    var cardCompanyCardId: String? = null,

    @Column(nullable = false)
    var benefitYearMonth: String? = null,

    @Column(nullable = false)
    var outcomeStartDay: String? = null,

    @Column(nullable = false)
    var outcomeEndDay: String? = null,

    @Column(columnDefinition = "TINYINT(1)", length = 1)
    var isOutcomeDelay: Boolean? = false,

    @Column(nullable = false, precision = 17, scale = 4)
    var beforeMonthCriteriaUseAmount: BigDecimal? = null,

    @Column(precision = 17, scale = 4)
    var outcomeCriteriaAmount: BigDecimal? = null,

    @Column(nullable = false, precision = 17, scale = 4)
    var totalBenefitAmount: BigDecimal? = null,

    var totalBenefitCount: Int? = null,

    @Column(precision = 17, scale = 4)
    var totalSalesAmount: BigDecimal? = null,

    @Column(precision = 9, scale = 4)
    var monthlyBenefitRate: BigDecimal? = null,

    @Column(precision = 17, scale = 4)
    var monthlyBenefitAmount: BigDecimal? = null,

    @Column(nullable = false, precision = 17, scale = 4)
    var monthlyBenefitLimit: BigDecimal? = null,

    @Column(precision = 17, scale = 4)
    var cashbackAmount: BigDecimal? = null,

    var benefitMessage: String? = null,

    var promotionCode: String? = null,

    @Column(nullable = false)
    var lastCheckAt: LocalDateTime? = null,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
) {
    fun equal(other: PlccCardThresholdEntity?): Boolean {
        val t = this.copy(plccCardBenefitLimitId = null, lastCheckAt = null, createdAt = null, updatedAt = null)
        val o = other?.copy(plccCardBenefitLimitId = null, lastCheckAt = null, createdAt = null, updatedAt = null)
        return t == o
    }
}
