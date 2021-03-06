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
@Table(name = "plcc_card_benefit_limit_history")
data class PlccCardThresholdHistoryEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var plccCardBenefitLimitHistoryId: Long? = null,

    @Column(nullable = false)
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

    var responseCode: String? = null,

    var responseMessage: String? = null,

    @Column(nullable = false)
    var lastCheckAt: LocalDateTime? = null,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)
