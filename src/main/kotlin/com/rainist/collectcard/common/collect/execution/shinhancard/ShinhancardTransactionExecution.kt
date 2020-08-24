package com.rainist.collectcard.common.collect.execution.shinhancard

import com.rainist.collect.common.api.Pagination
import com.rainist.collect.common.execution.Execution
import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponseDataBody
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.enums.ResultCode
import com.rainist.collectcard.common.exception.CollectExecutionExceptionHandler
import java.util.function.BinaryOperator

class ShinhancardTransactionExecution {

    companion object {
        /* 카드 이용내역 merge */
        val transactionsMerge =
            BinaryOperator { prev: ListTransactionsResponse, next: ListTransactionsResponse ->
                prev.resultCodes.add(next.dataHeader?.resultCode ?: ResultCode.UNKNOWN)

                val prevTransactions = prev.dataBody?.transactions ?: mutableListOf()
                val nextTransactions = next.dataBody?.transactions ?: mutableListOf()
                prevTransactions.addAll(nextTransactions)

                prev.dataBody = ListTransactionsResponseDataBody(transactions = prevTransactions, nextKey = next.dataBody?.nextKey)
                prev
            }

        val cardShinhancardTransactions =
            Execution.create()
                // 신용 국내사용내역조회-일시불/할부 SHC_HPG00428
                .exchange(ShinhancardApis.card_shinhancard_credit_domestic_transactions)
                .to(ListTransactionsResponse::class.java)
                .exceptionally { executionContext: ExecutionContext, throwable: Throwable ->
                    CollectExecutionExceptionHandler.handle(
                        executionContext,
                        Organization.shinhancard.name,
                        "cardShinhancardTransactions",
                        ShinhancardApis.card_shinhancard_credit_domestic_transactions.id,
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
                // 신용 해외사용내역조회-일시불조회 SHC_HPG01612
                .with(
                    Execution.create()
                        .exchange(ShinhancardApis.card_shinhancard_credit_oversea_transactions)
                        .to(ListTransactionsResponse::class.java)
                        .exceptionally { executionContext: ExecutionContext, throwable: Throwable ->
                            CollectExecutionExceptionHandler.handle(
                                executionContext,
                                Organization.shinhancard.name,
                                "cardShinhancardTransactions",
                                ShinhancardApis.card_shinhancard_credit_oversea_transactions.id,
                                throwable
                            )
                        }
                        .paging(
                            Pagination.builder()
                                .method(Pagination.Method.NEXTKEY)
                                .nextkey(".dataBody.nextKey")
                                .merge(transactionsMerge)
                                .build()
                        ).build()
                )
                // 체크 국내사용내역 조회 SHC_HPG01030
                .with(
                    Execution.create()
                        .exchange(ShinhancardApis.card_shinhancard_check_domestic_transactions)
                        .to(ListTransactionsResponse::class.java)
                        .exceptionally { executionContext: ExecutionContext, throwable: Throwable ->
                            CollectExecutionExceptionHandler.handle(
                                executionContext,
                                Organization.shinhancard.name,
                                "cardShinhancardTransactions",
                                ShinhancardApis.card_shinhancard_check_domestic_transactions.id,
                                throwable
                            )
                        }
                        .paging(
                            Pagination.builder()
                                .method(Pagination.Method.NEXTKEY)
                                .nextkey(".dataBody.nextKey")
                                .merge(transactionsMerge)
                                .build()
                        ).build()
                )

                // 체크 해외사용내역 조회 SHC_HPG01031
                .with(
                    Execution.create()
                        .exchange(ShinhancardApis.card_shinhancard_check_oversea_transactions)
                        .to(ListTransactionsResponse::class.java)
                        .exceptionally { executionContext: ExecutionContext, throwable: Throwable ->
                            CollectExecutionExceptionHandler.handle(
                                executionContext,
                                Organization.shinhancard.name,
                                "cardShinhancardTransactions",
                                ShinhancardApis.card_shinhancard_check_oversea_transactions.id,
                                throwable
                            )
                        }
                        .paging(
                            Pagination.builder()
                                .method(Pagination.Method.NEXTKEY)
                                .nextkey(".dataBody.nextKey")
                                .merge(transactionsMerge)
                                .build()
                        ).build()
                )
                .merge(transactionsMerge)
                .build()
    }
}
