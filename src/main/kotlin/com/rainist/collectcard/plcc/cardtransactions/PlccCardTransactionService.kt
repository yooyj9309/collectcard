package com.rainist.collectcard.plcc.cardtransactions

import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.plcc.cardtransactions.dto.PlccCardTransactionResponse

interface PlccCardTransactionService {
    fun plccCardTransactions(executionContext: CollectExecutionContext): PlccCardTransactionResponse
}
