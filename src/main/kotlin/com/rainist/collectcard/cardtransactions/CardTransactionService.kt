package com.rainist.collectcard.cardtransactions

import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse
import com.rainist.collectcard.common.dto.CollectExecutionContext

interface CardTransactionService {
    fun listTransactions(executionContext: CollectExecutionContext): ListTransactionsResponse
}
