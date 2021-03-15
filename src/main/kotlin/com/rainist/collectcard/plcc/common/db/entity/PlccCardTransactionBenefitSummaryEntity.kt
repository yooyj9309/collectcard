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
@Table(name = "plcc_card_transaction_benefit_summary")
data class PlccCardTransactionBenefitSummaryEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var plccCardTransactionBenefitSummaryId: Long? = null,

    var approvalYearMonth: String? = null,

    @Column(nullable = false)
    var banksaladUserId: Long? = null,

    @Column(nullable = false)
    var cardCompanyId: String? = null,

    @Column(nullable = false)
    var cardCompanyCardId: String? = null,

    var totalBenefitCount: Int? = null,

    var totalBenefitAmount: BigDecimal? = null,

    var totalSalesAmount: BigDecimal? = null,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)
