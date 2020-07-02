package com.rainist.collectcard.cardbills.dto

data class ListBillTransactionsResponse(
    var dataHeader: ListBillTransactionsResponseDataHeader? = null,
    var dataBody: ListBillTransactionsResponseDataBody? = null
)

data class ListBillTransactionsResponseDataHeader(
    var resultCode: String? = null,
    var resultMessage: String? = null
)

data class ListBillTransactionsResponseDataBody(
    var billTransactions: MutableList<CardBillTransaction>? = null
)
