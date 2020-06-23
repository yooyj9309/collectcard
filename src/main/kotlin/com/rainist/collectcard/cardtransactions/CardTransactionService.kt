package com.rainist.collectcard.cardtransactions

import com.rainist.collectcard.cardtransactions.dto.ListTransactionsRequest
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse

interface CardTransactionService {
    fun listTransactions(listTransactionsRequest: ListTransactionsRequest): ListTransactionsResponse
}
