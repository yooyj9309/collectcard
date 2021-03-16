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
import java.math.BigDecimal
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

                // 전체 summary 는 제일 처음에 주는 값을 사용  sum 금지
                val totalBenefitAmount = prev.dataBody?.totalBenefitAmount ?: BigDecimal.ZERO
                val totalBenefitCount = prev.dataBody?.totalBenefitCount ?: 0
                val totalSalesAmount = prev.dataBody?.totalSalesAmount ?: BigDecimal.ZERO

                prev.dataBody = PlccCardTransactionResponseDataBody(
                    totalBenefitAmount = totalBenefitAmount,
                    totalBenefitCount = totalBenefitCount,
                    totalSalesAmount = totalSalesAmount,
                    transactionList = prevTransactions,
                    nextKey = next.dataBody?.nextKey,
                    paginationResultCode = next.dataBody?.paginationResultCode
                )
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
                        .max(200)
                        .build()
                )
                .build()
    }
}
