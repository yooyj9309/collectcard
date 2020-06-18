package com.rainist.collectcard.cardtransactions.dto

enum class CardType(
    var index: Int,
    var description: String
) {

    UNKNOWN(0, "알수없음"),
    DEBIT(1, "체크카드"),
    CREDIT(2, "신용카드"),
    MICROPAYMENT(3, "하이브리드카드")
    ;
}
