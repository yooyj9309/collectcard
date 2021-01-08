package com.rainist.collectcard.cardtransactions

import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse
import com.rainist.collectcard.common.dto.CollectExecutionContext
import java.time.LocalDateTime

interface CardTransactionService {
    fun listTransactions(executionContext: CollectExecutionContext, now: LocalDateTime): ListTransactionsResponse
}
