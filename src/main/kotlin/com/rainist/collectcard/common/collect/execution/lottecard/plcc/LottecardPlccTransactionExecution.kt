package com.rainist.collectcard.common.collect.execution.lottecard.plcc

import com.rainist.collect.common.api.Pagination
import com.rainist.collect.common.execution.Execution
import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collectcard.common.collect.api.LottecardPlccApis
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.enums.ResultCode
import com.rainist.collectcard.common.exception.CollectExecutionExceptionHandler
import com.rainist.collectcard.plcc.cardtransactions.dto.PlccCardTransactionResponse
import com.rainist.collectcard.plcc.cardtransactions.dto.PlccCardTransactionResponseDataBody
import java.util.function.BinaryOperator

class LottecardPlccTransactionExecution {

    companion object {

        /* 카드 이용내역 merge */
        private val transactionsMerge =
            BinaryOperator { prev: PlccCardTransactionResponse, next: PlccCardTransactionResponse ->
                prev.dataBody?.responseCode = next.dataBody?.responseCode ?: ResultCode.UNKNOWN
                prev.dataBody?.responseMessage = next.dataBody?.responseMessage

                val prevTransactions = prev.dataBody?.transactionList?.toMutableList() ?: mutableListOf()
                val nextTransactions = next.dataBody?.transactionList?.toMutableList() ?: mutableListOf()
                prevTransactions.addAll(nextTransactions)

                prev.dataBody = PlccCardTransactionResponseDataBody(transactionList = prevTransactions, nextKey = next.dataBody?.nextKey)
                prev
            }

        val lottecardPlccTransactions: Execution =
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
                .paging(
                    Pagination.builder()
                        .method(Pagination.Method.NEXTKEY)
                        .nextkey(".dataBody.nextKey")
                        .merge(transactionsMerge)
                        .build()
                )
                .build()
    }
}
