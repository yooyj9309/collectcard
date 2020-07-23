package com.rainist.collectcard.common.db.entity

import java.math.BigDecimal
import java.time.LocalDateTime
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
@Table(name = "card_loan")
data class CardLoanEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var cardLoanId: Long? = null,

    var banksaladUserId: Long? = null,

    var cardCompanyId: String? = null,

    var cardCompanyLoanId: String? = null,

    var lastCheckAt: LocalDateTime? = null,

    var loanName: String? = null,

    var paymentBankId: String? = null,

    var paymentAccountNumber: String? = null,

    var expirationDay: String? = null,

    var loanStatus: String? = null,

    var loanStatusOrigin: String? = null,

    var repaymentMethod: String? = null,

    var repaymentMethodOrigin: String? = null,

    var withdrawalDay: String? = null,

    var interestRate: BigDecimal? = null,

    var loanCategory: String? = null,

    var currencyCode: String? = null,

    var additionalLoanAmount: BigDecimal? = null,

    var fullyPaidDay: String? = null,

    var cardNumber: String? = null,

    var principalAmount: BigDecimal? = null,

    var interestAmount: BigDecimal? = null,

    var loanNumber: String? = null,

    var loanAmount: BigDecimal? = null,

    var loanRemainingAmount: BigDecimal? = null,

    // 생성일시
    @CreatedDate
    var createdAt: LocalDateTime? = null,

    // 수정일시
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null

)
