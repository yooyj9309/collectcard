package com.rainist.collectcard.cardcreditlimit.entity

import java.math.BigDecimal
import java.sql.Timestamp
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

/**
 * 카드 한도 이력
 */
@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(name = "card_limit_history")
data class CreditLimitHistoryEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var cardLimitHistoryId: Long? = null,

    // 카드한도 ID
    var cardLimitId: Long? = null,

    // 뱅크샐러드 사용자 ID
    var banksaladUserId: Long? = null,

    // 카드 회사 ID
    var cardCompanyId: String? = null,

    // 최종조회일시
    var lastCheckAt: Timestamp? = null,

    // 일회결제한도금액
    var onetimePaymentLimitAmount: BigDecimal? = null,

    // 신용카드 총한도금액
    var creditCardLimitTotalAmount: BigDecimal? = null,

    // 신용카드 사용한도금액
    var creditCardLimitUsedAmount: BigDecimal? = null,

    // 신용카드 잔여한도금액
    var creditCardLimitRemainingAmount: BigDecimal? = null,

    // 현금서비스 총한도금액
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
    var loanLimitTotalAmount: BigDecimal? = null,

    // 대출 잔여한도금액
    var loanLimitRemainingAmount: BigDecimal? = null,

    // 대출 사용한도금액
    var loanLimitUsedAmount: BigDecimal? = null,

    // 카드론 총한도금액
    var cardLoanLimitTotalAmount: BigDecimal? = null,

    // 카드론 사용한도금액
    var cardLoanLimitUsedAmount: BigDecimal? = null,

    // 카드론 잔여한도금액
    var cardLoanLimitRemainingAmount: BigDecimal? = null,

    // 직불카드 총금액
    var debitCardTotalAmount: BigDecimal? = null,

    // 직불카드 사용금액
    var debitCardUsedAmount: BigDecimal? = null,

    // 직불카드 잔여금액
    var debitCardRemainingAmount: BigDecimal? = null,

    // 생성일시
    @CreatedDate
    var createdAt: LocalDateTime? = null,

    // 수정일시
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)
