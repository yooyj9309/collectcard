package com.rainist.collectcard.cardloans.dto

import javax.validation.constraints.NotEmpty

class ListLoansRequest(
    var dataHeader: ListLoansRequestDataHeader? = null,
    var dataBody: ListLoansRequestDataBody? = null
)

data class ListLoansRequestDataHeader(
    var empty: Any? = null
)

data class ListLoansRequestDataBody(

    var loanNumber: String? = null, // 대출번호

    var isCardLoan: Boolean? = null, // 론 고객 여부

    @field:NotEmpty
    var nextKey: String? = "" // pagination 방식 중 nextKey
)
