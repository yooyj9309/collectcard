package com.rainist.collectcard.cardbills.dto

data class ListBillTransactionsRequest(
    var dataHeader: ListBillTransactionsRequestDataHeader? = null,
    var dataBody: ListBillTransactionsRequestDataBody? = null
)

data class ListBillTransactionsRequestDataHeader(
    var empty: Any
)

data class ListBillTransactionsRequestDataBody(
    var billId: String,
    var nextKey: String
)
