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

@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(name = "card_limit")
data class CreditLimitEntity(

    // 카드 한도 테이블 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var cardLimitId: Long? = null,

    // 뱅샐 유저 아이디
    @Column(nullable = false)
    var banksaladUserId: Long? = null,

    // 카드 회사 ID
    @Column(nullable = false)
    var cardCompanyId: String? = null,

    // 최종조회일시
    @Column(nullable = false)
    var lastCheckAt: LocalDateTime? = null,

    // 일회결제한도금액
    @Column(nullable = false)
    var onetimePaymentLimitAmount: BigDecimal? = null,

    // 일회결제 사용한도금액
    var onetimePaymentLimitUsedAmount: BigDecimal? = null,

    // 일회결제 잔여한도금액
    var onetimePaymentLimitRemainingAmount: BigDecimal? = null,

    // 신용카드 총 한도금액
    @Column(nullable = false)
    var creditCardLimitTotalAmount: BigDecimal? = null,

    // 신용카드 사용한도금액
    var creditCardLimitUsedAmount: BigDecimal? = null,

    // 신용카드 잔여한도금액
    var creditCardLimitRemainingAmount: BigDecimal? = null,

    // 현금서비스 총 한도 금액
    @Column(nullable = false)
    var cashAdvanceLimitTotalAmount: BigDecimal? = null,

    // 현금서비스 사용한도금액
    var cashAdvanceLimitUsedAmount: BigDecimal? = null,

    // 현금서비스 잔여한도금액
    var cashAdvanceLimitRemainingAmount: BigDecimal? = null,

    // 해외 총한도금액
    var overseaLimitTotalAmount: BigDecimal? = null,

    // 해외 사용한도금액
    var overseaLimitUsedAmount: BigDecimal? = null,

    // 해외 잔여한도금액
    var overseaLimitRemainingAmount: BigDecimal? = null,

    // 대출 총한도금액
    @Column(nullable = false)
    var loanLimitTotalAmount: BigDecimal? = null,

    // 대출 잔여한도금액
    var loanLimitRemainingAmount: BigDecimal? = null,

    // 대출 사용한도금액
    var loanLimitUsedAmount: BigDecimal? = null,

    // 카드론 총한도금액
    @Column(nullable = false)
    var cardLoanLimitTotalAmount: BigDecimal? = null,

    // 카드론 사용한도금액
    var cardLoanLimitUsedAmount: BigDecimal? = null,

    // 카드론 잔여한도금액
    var cardLoanLimitRemainingAmount: BigDecimal? = null,

    // 직불카드 총금액
    @Column(nullable = false)
    var debitCardTotalAmount: BigDecimal? = null,

    // 직불카드 사용금액
    var debitCardUsedAmount: BigDecimal? = null,

    // 직불카드 잔여금액
    var debitCardRemainingAmount: BigDecimal? = null,

    // 할부 총한도금액
    var installmentLimitTotalAmount: BigDecimal? = null,

    // 할부 사용한도금액
    var installmentLimitUsedAmount: BigDecimal? = null,

    // 할부 잔여한도금액
    var installmentLimitRemainingAmount: BigDecimal? = null,

    // 생성일시
    @CreatedDate
    var createdAt: LocalDateTime? = null,

    // 수정일시
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)
