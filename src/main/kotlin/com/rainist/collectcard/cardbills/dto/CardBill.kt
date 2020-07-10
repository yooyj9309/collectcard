package com.rainist.collectcard.cardbills.dto

import java.math.BigDecimal
import java.time.ZonedDateTime

data class CardBill(
    var billId: String?,

    // 마스킹 된 고객 이름 (e.g. 김X샐)
    var userName: String?,

    // 사용자 등급 (e.g. 마스터, 프리미엄, 실버, VIP)
    var userGrade: String?,

    // 청구번호
    var billNumber: String?,

    // 결제예정일 또는 결제일
    var paymentDate: ZonedDateTime?,

    // 다음 결제 예정일
    var nextPaymentDate: ZonedDateTime?,

    // 청구금액
    var billingAmount: BigDecimal?,

    // 선결제 된 금액
    var prepayedAmount: BigDecimal?,

    // 결제기관
    var paymentBankId: String?,

    // 결제계좌번호
    var paymentAccountNumber: String?,

    // 청구년월
    var billedYearMonth: ZonedDateTime?,

    // 포인트
    var totalPoints: Int?,

    // 소멸 예정 포인트
    var expiringPoints: Int?,

    // 결제예정금액 상세
    var transactions: List<CardBillTransaction>?
)
