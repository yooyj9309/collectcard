package com.rainist.collectcard.common.enums

enum class CardType(
    var index: Int,
    var description: String
) {

    CHECK(0, "(Diff용,구)체크카드"),
    DEBIT(1, "체크카드"),
    CREDIT(2, "신용카드"),
    MICROPAYMENT(3, "하이브리드카드"),
    UNKNOWN(99, "알수없음"),
    ;
}
