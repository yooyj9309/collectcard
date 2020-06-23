package com.rainist.collectcard.cardbills.dto

import com.rainist.collectcard.cardtransactions.dto.CardTransaction

data class ListBillTransactionsResponse(
    var billTransactions: List<CardTransaction>
)
