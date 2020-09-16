package com.rainist.collectcard.cardbills.dto

import java.math.BigDecimal

enum class BillCardType {
    CREDIT,
    DEBIT,
    ALL,
    UNKNOWN
}

data class CardBill(
    var billId: String? = null,

    // 청구번호
    var billNumber: String? = null,

    // 명세서제목 또는 발급사등 구분
    var billType: String? = null,

    // 명세서제목 또는 발급사등 구분
    var cardType: BillCardType? = BillCardType.UNKNOWN,

    // 마스킹 된 고객 이름 (e.g. 김X샐)
    var userName: String? = null,

    // 사용자 등급 (e.g. 마스터, 프리미엄, 실버, VIP)
    var userGrade: String? = null,

    // 사용자등급 원본 값
    var userGradeOrigin: String? = null,

    // 결제예정일 또는 결제일
    var paymentDay: String? = null,

    // 청구년월
    var billedYearMonth: String? = null,

    // 다음 결제 예정일
    var nextPaymentDay: String? = null,

    // 청구금액
    var billingAmount: BigDecimal? = null,

    // 선결제 된 금액
    var prepaidAmount: BigDecimal? = null,

    // 결제기관
    var paymentBankId: String? = null,

    // 결제계좌번호
    var paymentAccountNumber: String? = null,

    // 포인트
    var totalPoints: Int? = null,

    // 소멸 예정 포인트
    var expiringPoints: Int? = null,

    // 결제예정금액 상세
    var transactions: MutableList<CardBillTransaction>? = null,

    var dataBody: CardBillNextKey? = null
)

// nextKey 이슈로 인하여 2depth에 적용
class CardBillNextKey {
    var nextKey: String = ""
}
