package com.rainist.collectcard.plcc.cardtransactions

import com.github.banksalad.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.collectcard.common.dto.CollectExecutionContext

interface PlccCardTransactionService {
    fun plccCardTransactions(executionContext: CollectExecutionContext, plccCardTransactionRequest: CollectcardProto.ListPlccRewardsTransactionsRequest)
}
