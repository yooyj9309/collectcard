package com.rainist.collectcard.common.enums

enum class CardLoanStatus(
    var index: Int,
    var description: String
) {

    CardLOAN_STATUS_UNKNOWN(0, "상태알수없음"),
    CardLOAN_STATUS_UNPAID(1, "미납"),
    CardLOAN_STATUS_FULLY_PAID(2, "완납")
    ;
}
