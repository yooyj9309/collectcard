package com.rainist.collectcard.common.db.entity

import com.rainist.collectcard.cardloans.dto.Loan
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

fun CardLoanEntity.makeCardLoanEntity(banksaladUserId: String, organizationId: String?, loan: Loan): CardLoanEntity {
    this.banksaladUserId = banksaladUserId.toLong()
    this.cardCompanyId = organizationId
    this.cardCompanyLoanId = loan.loanId
    this.loanName = loan.loanName
    this.paymentBankId = loan.paymentBankId
    this.expirationDay = loan.expirationDay
    this.loanStatus = loan.loanStatus.name
    this.loanStatusOrigin = loan.loanStatusOrigin
    this.paymentAccountNumber = loan.paymentAccountNumber
    this.repaymentMethod = loan.repaymentMethod.name
    this.repaymentMethodOrigin = loan.repaymentMethodOrigin
    this.withdrawalDay = loan.withdrawalDay
    this.interestRate = loan.interestRate ?: BigDecimal(0) // default 0
    this.loanCategory = loan.loanCategory
    this.currencyCode = "KRW" // todo 해당부분이 loan에 없음 확인필요
    this.additionalLoanAmount = loan.additionalLoanAmount ?: BigDecimal(0) // default 0
    this.fullyPaidDay = loan.fullyPaidDay
    this.cardNumber = loan.cardNumber
    this.principalAmount = loan.principalAmount ?: BigDecimal(0) // default 0
    this.interestAmount = loan.interestAmount ?: BigDecimal(0) // default 0
    this.loanNumber = loan.loanNumber
    this.loanAmount = loan.loanAmount ?: BigDecimal(0) // default 0
    this.loanRemainingAmount = loan.remainingAmount ?: BigDecimal(0) // default 0
    return this
}
