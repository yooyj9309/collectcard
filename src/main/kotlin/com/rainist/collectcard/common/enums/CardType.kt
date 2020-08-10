package com.rainist.collectcard.common.enums

enum class CardType(
    var index: Int,
    var description: String,
    var jg: String?
) {

    CHECK(0, "(Diff용,구)체크카드", null), // TODO diff 끝나고 원복 "check"),
    DEBIT(1, "체크카드", null), // TODO diff 끝나고 원복"check"),
    CREDIT(2, "신용카드", null), // TODO diff 끝나고 원복 "credit"),
    MICROPAYMENT(3, "하이브리드카드", null),
    UNKNOWN(99, "알수없음", null),
    ;
}
