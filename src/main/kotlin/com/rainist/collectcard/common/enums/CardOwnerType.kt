package com.rainist.collectcard.common.enums

enum class CardOwnerType(
    var index: Int,
    var description: String
) {

    SELF(1, "본인카드"),
    FAMILY(2, "가족카드"),
    BUSINESS(3, "법인카드"),
    UNKNOWN(99, "알수없음")
    ;
}
