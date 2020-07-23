package com.rainist.collectcard.cardtransactions

import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse
import com.rainist.collectcard.common.dto.SyncRequest

interface CardTransactionService {
    fun listTransactions(syncRequest: SyncRequest, fromMs: Long?): ListTransactionsResponse
}
