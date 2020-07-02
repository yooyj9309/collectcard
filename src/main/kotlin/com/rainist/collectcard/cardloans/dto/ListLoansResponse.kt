package com.rainist.collectcard.cardloans.dto

data class ListLoansResponse(
    var dataHeader: ListLoansResponseDataHeader? = null,
    var dataBody: ListLoansResponseDataBody? = null
)

data class ListLoansResponseDataHeader(
    var successCode: String? = null,
    var resultCode: String? = null,
    var resultMessage: String? = null
)

data class ListLoansResponseDataBody(
    var loans: MutableList<Loan>? = null,
    var nextKey: String? = null
)
