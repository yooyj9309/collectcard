package com.rainist.collectcard.common.enums

enum class CardOwnerType(
    var index: Int,
    var description: String
) {

    PERSONAL(1, "본인카드"),
    FAMILY(2, "가족카드"),
    ;
}
