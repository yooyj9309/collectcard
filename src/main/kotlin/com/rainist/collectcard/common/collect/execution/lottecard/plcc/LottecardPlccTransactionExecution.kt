package com.rainist.collectcard.common.collect.execution.lottecard.plcc

import com.rainist.collect.common.execution.Execution
import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collectcard.common.collect.api.LottecardPlccApis
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.exception.CollectExecutionExceptionHandler
import com.rainist.collectcard.plcc.cardtransactions.dto.PlccCardTransactionResponse

class LottecardPlccTransactionExecution {

    companion object {

        val lottecardPlccTransactions =
            Execution.create()
                .exchange(LottecardPlccApis.card_lottecard_plcc_transactions)
                .to(PlccCardTransactionResponse::class.java)
                .exceptionally { executionContext: ExecutionContext, throwable: Throwable ->
                    CollectExecutionExceptionHandler.handle(
                        executionContext,
                        Organization.lottecard.name,
                        "LottecardPlccTransactions",
                        LottecardPlccApis.card_lottecard_plcc_transactions.id,
                        throwable
                    )
                }
                .build()
    }
}
