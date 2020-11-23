package com.rainist.collectcard.common.db.entity

import com.rainist.collectcard.common.converter.CardLoanEncryptConverter
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Convert
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
@Table(name = "card_loan_history")
data class CardLoanHistoryEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var cardLoanHistoryId: Long? = null,

    @Column(nullable = false)
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

    @Convert(converter = CardLoanEncryptConverter::class)
    @Column(nullable = true, name = "paymentAccountNumberEncrypted")
    var paymentAccountNumber: String? = null,

    @Column(nullable = false)
    var expirationDay: String? = null,

    @Column(nullable = false)
    var loanStatus: String? = null,

    var loanStatusOrigin: String? = null,

    var repaymentMethod: String? = null,

    var repaymentMethodOrigin: String? = null,

    var withdrawalDay: String? = null,

    var interestRate: BigDecimal? = null,

    @Column(nullable = false)
    var loanCategory: String? = null,

    var currencyCode: String? = null,

    var additionalLoanAmount: BigDecimal? = null,

    var fullyPaidDay: String? = null,

    @Convert(converter = CardLoanEncryptConverter::class)
    @Column(nullable = true, name = "cardNumberEncrypted")
    var cardNumber: String? = null,

    var principalAmount: BigDecimal? = null,

    var interestAmount: BigDecimal? = null,

    @Column(nullable = false)
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

fun CardLoanHistoryEntity.makeCardLoanHistoryEntity(cardLoanEntity: CardLoanEntity, now: LocalDateTime): CardLoanHistoryEntity {
    this.cardLoanId = cardLoanEntity.cardLoanId
    this.banksaladUserId = cardLoanEntity.banksaladUserId
    this.cardCompanyId = cardLoanEntity.cardCompanyId
    this.cardCompanyLoanId = cardLoanEntity.cardCompanyLoanId
    this.lastCheckAt = now
    this.loanName = cardLoanEntity.loanName
    this.paymentBankId = cardLoanEntity.paymentBankId
    this.expirationDay = cardLoanEntity.expirationDay
    this.loanStatus = cardLoanEntity.loanStatus
    this.loanStatusOrigin = cardLoanEntity.loanStatusOrigin
    this.paymentAccountNumber = cardLoanEntity.paymentAccountNumber
    this.repaymentMethod = cardLoanEntity.repaymentMethod
    this.repaymentMethodOrigin = cardLoanEntity.repaymentMethodOrigin
    this.withdrawalDay = cardLoanEntity.withdrawalDay
    this.interestRate = cardLoanEntity.interestRate
    this.loanCategory = cardLoanEntity.loanCategory
    this.currencyCode = cardLoanEntity.currencyCode
    this.additionalLoanAmount = cardLoanEntity.additionalLoanAmount
    this.fullyPaidDay = cardLoanEntity.fullyPaidDay
    this.cardNumber = cardLoanEntity.cardNumber
    this.principalAmount = cardLoanEntity.principalAmount
    this.interestAmount = cardLoanEntity.interestAmount
    this.loanNumber = cardLoanEntity.loanNumber
    this.loanAmount = cardLoanEntity.loanAmount
    this.loanRemainingAmount = cardLoanEntity.loanRemainingAmount

    return this
}
