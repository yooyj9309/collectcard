package com.rainist.collectcard.cardloans.dto

enum class LoanStatus(
    var index: Int,
    var description: String
) {

    LOAN_STATUS_UNKNOWN(0, "상태알수없음"),
    LOAN_STATUS_UNPAID(1, "미납"),
    LOAN_STATUS_FULLY_PAID(2, "완납"),
    ;
}
