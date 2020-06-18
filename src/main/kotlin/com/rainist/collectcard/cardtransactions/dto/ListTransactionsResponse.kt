package com.rainist.collectcard.cardtransactions.dto

data class ListTransactionsResponse(
    var dataHeader: ListTransactionsResponseDataHeader? = null,
    var dataBody: ListTransactionsResponseDataBody? = null
)

data class ListTransactionsResponseDataHeader(
    var successCode: String? = null,
    var resultCode: String? = null,
    var resultMessage: String? = null
)

data class ListTransactionsResponseDataBody(
    var transactions: List<CardTransaction>? = null,
    var nextKey: String? = null
)
