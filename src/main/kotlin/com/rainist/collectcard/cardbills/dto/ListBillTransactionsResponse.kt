package com.rainist.collectcard.cardbills.dto

import com.rainist.collectcard.common.enums.ResultCode

data class ListBillTransactionsResponse(
    var resultCodes: MutableList<ResultCode> = mutableListOf(),
    var dataHeader: ListBillTransactionsResponseDataHeader? = null,
    var dataBody: ListBillTransactionsResponseDataBody? = null
)

data class ListBillTransactionsResponseDataHeader(
    var resultCode: ResultCode? = null,
    var resultMessage: String? = null,
    var successCode: String? = null
)

data class ListBillTransactionsResponseDataBody(
    var billTransactions: MutableList<CardBillTransaction>? = null,
    var nextKey: String? = ""

)
