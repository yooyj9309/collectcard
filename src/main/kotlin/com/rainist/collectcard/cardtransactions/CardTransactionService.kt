package com.rainist.collectcard.cardtransactions

import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse

interface CardTransactionService {
    fun listTransactions(executionContext: ExecutionContext, fromMs: Long?): ListTransactionsResponse
}
