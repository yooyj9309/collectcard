package com.rainist.collectcard.cardtransactions.dto

enum class CardTransactionType(
    var index: Int,
    var description: String
) {

    APPROVAL(1, "승인 ( 승인만 나있는 경우 )"),
    APPROVAL_CANCEL(2, "승인 취소"),
    APPROVAL_PART_CANCEL(3, "부분 승인 취소"),
    PURCHASE(4, "매입"),
    PURCHASE_CANCEL(5, "매입 취소"),
    PURCHASE_PART_CANCEL(6, "매입 부분 취소"),
    CARD_TRANSACTION_TYPE_UNKNOWN(7, "모르는 타입"),
    SINGLE_PAYMENT(8, "일시불"),
    CASH_SERVICE(9, "현금서비스")
    ;
}
