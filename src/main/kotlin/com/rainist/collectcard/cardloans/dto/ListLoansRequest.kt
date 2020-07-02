package com.rainist.collectcard.cardloans.dto

import javax.validation.constraints.NotNull

class ListLoansRequest(
    var dataHeader: ListLoansRequestDataHeader? = null,
    var dataBody: ListLoansRequestDataBody? = null
)

data class ListLoansRequestDataHeader(
    var empty: Any? = null
)

data class ListLoansRequestDataBody(

    var loanNumber: String? = null, // 대출번호

    @field:NotNull
    var nextKey: String? = "" // pagination 방식 중 nextKey
)
