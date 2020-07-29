package com.rainist.collectcard.common.db.entity

import com.rainist.collectcard.cardloans.dto.Loan
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

@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(name = "card_loan")
data class CardLoanEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var cardLoanId: Long? = null,

    @Column(nullable = false)
    var banksaladUserId: Long? = null,

    @Column(nullable = false)
    var cardCompanyId: String? = null,

    @Column(nullable = false)
    var cardCompanyLoanId: String? = null,

    @Column(nullable = false)
    var lastCheckAt: LocalDateTime? = null,

    @Column(nullable = false)
    var loanName: String? = null,

    var paymentBankId: String? = null,

    var paymentAccountNumber: String? = null,

    @Column(nullable = false)
    var expirationDay: String? = null,

    @Column(nullable = false)
    var loanStatus: String? = null,

    var loanStatusOrigin: String? = null,

    var repaymentMethod: String? = null,

    var repaymentMethodOrigin: String? = null,

    var withdrawalDay: String? = null,

    @Column(precision = 9, scale = 4)
    var interestRate: BigDecimal? = null,

    @Column(nullable = false)
    var loanCategory: String? = null,

    var currencyCode: String? = null,

    @Column(precision = 17, scale = 4)
    var additionalLoanAmount: BigDecimal? = null,

    var fullyPaidDay: String? = null,

    var cardNumber: String? = null,

    @Column(precision = 17, scale = 4)
    var principalAmount: BigDecimal? = null,

    @Column(precision = 17, scale = 4)
    var interestAmount: BigDecimal? = null,

    @Column(nullable = false)
    var loanNumber: String? = null,

    @Column(precision = 17, scale = 4)
    var loanAmount: BigDecimal? = null,

    @Column(precision = 17, scale = 4)
    var loanRemainingAmount: BigDecimal? = null,

    // 생성일시
    @CreatedDate
    var createdAt: LocalDateTime? = null,

    // 수정일시
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null

)

fun CardLoanEntity.makeCardLoanEntity(lastCheckAt: LocalDateTime?, banksaladUserId: String, organizationId: String?, loan: Loan): CardLoanEntity {
    this.banksaladUserId = banksaladUserId.toLong()
    this.cardCompanyId = organizationId
    this.cardCompanyLoanId = loan.loanId
    this.lastCheckAt = lastCheckAt ?: DateTimeUtil.utcNowLocalDateTime()
    this.loanName = loan.loanName ?: ""
    this.paymentBankId = loan.paymentBankId
    this.expirationDay = loan.expirationDay
    this.loanStatus = loan.loanStatus.name
    this.loanStatusOrigin = loan.loanStatusOrigin
    this.paymentAccountNumber = loan.paymentAccountNumber
    this.repaymentMethod = loan.repaymentMethod.name
    this.repaymentMethodOrigin = loan.repaymentMethodOrigin
    this.withdrawalDay = loan.withdrawalDay
    this.interestRate = loan.interestRate?.setScale(4) ?: BigDecimal(0).setScale(4) // default 0
    this.loanCategory = loan.loanCategory ?: ""
    this.currencyCode = "KRW" // todo 해당부분이 loan에 없음 확인필요
    this.additionalLoanAmount = loan.additionalLoanAmount?.setScale(4) ?: BigDecimal(0).setScale(4) // default 0
    this.fullyPaidDay = loan.fullyPaidDay
    this.cardNumber = loan.cardNumber
    this.principalAmount = loan.principalAmount?.setScale(4) ?: BigDecimal(0).setScale(4) // default 0
    this.interestAmount = loan.interestAmount?.setScale(4) ?: BigDecimal(0).setScale(4) // default 0
    this.loanNumber = loan.loanNumber ?: ""
    this.loanAmount = loan.loanAmount?.setScale(4) ?: BigDecimal(0).setScale(4) // default 0
    this.loanRemainingAmount = loan.remainingAmount?.setScale(4) ?: BigDecimal(0).setScale(4) // default 0
    return this
}
