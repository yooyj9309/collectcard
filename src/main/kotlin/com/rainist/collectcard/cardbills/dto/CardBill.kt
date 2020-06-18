package com.rainist.collectcard.cardbills.dto

import java.math.BigDecimal
import java.time.ZonedDateTime

data class CardBill(
    var bill_id: String,

    // 마스킹 된 고객 이름 (e.g. 김X샐)
    var user_name: String,

    // 사용자 등급 (e.g. 마스터, 프리미엄, 실버, VIP)
    var user_grade: String?,

    // 청구번호
    var bill_number: String?,

    // 결제예정일 또는 결제일
    var payment_date: ZonedDateTime,

    // 다음 결제 예정일
    var next_payment_date: ZonedDateTime,

    // 청구금액
    var billing_amount: BigDecimal,

    // 선결제 된 금액
    var prepayed_amount: BigDecimal?,

    // 결제기관
    var payment_bank_id: String?,

    // 결제계좌번호
    var payment_account_number: String?,

    // 청구년월
    var billed_year_month: ZonedDateTime,

    // 포인트
    var total_points: Int,

    // 소멸 예정 포인트
    var expiring_points: Int
)
