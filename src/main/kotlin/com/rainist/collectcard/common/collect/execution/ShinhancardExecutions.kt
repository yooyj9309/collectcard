package com.rainist.collectcard.common.collect.execution

import com.rainist.collect.common.dto.Execution
import com.rainist.collect.common.dto.Pagination
import com.rainist.collectcard.card.dto.ListCardsResponse
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import java.util.function.BinaryOperator

class ShinhancardExecutions {

    companion object {
        val cardShinhancardCards =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_cards)
                .to(ListCardsResponse::class.java)
                .build()

        val cardShinhancardTransactionsMerge = BinaryOperator { prevTransactions: ListTransactionsResponse, nextTransactions: ListTransactionsResponse ->

            prevTransactions.dataBody?.transactions?.addAll(nextTransactions.dataBody?.transactions ?: mutableListOf())

            ListTransactionsResponse().apply {
                dataHeader = prevTransactions.dataHeader
                dataBody = prevTransactions.dataBody
            }
        }

        val cardShinhancardTransactions =
            Execution.create()
                // 신용 국내사용내역조회-일시불/할부 SHC_HPG00428
                .exchange(ShinhancardApis.card_shinhancard_credit_domestic_transactions)
                .to(ListTransactionsResponse::class.java)
                .pagination(
                    Pagination.builder()
                        .method(Pagination.Method.NEXTKEY)
                        .nextkey("nextKey")
                        .build()
                )
                .merge(cardShinhancardTransactionsMerge)
                // 신용 해외사용내역조회-일시불조회 SHC_HPG01612
                .with(
                    Execution.create()
                        .exchange(ShinhancardApis.card_shinhancard_credit_oversea_transactions)
                        .to(ListTransactionsResponse::class.java)
                        .pagination(
                            Pagination.builder()
                                .method(Pagination.Method.NEXTKEY)
                                .nextkey("nextKey")
                                .build()
                        )
                        .merge(cardShinhancardTransactionsMerge)
                        .build()
                )
                // 체크 국내사용내역 조회 SHC_HPG01030
                .with(
                    Execution.create()
                        .exchange(ShinhancardApis.card_shinhancard_check_domestic_transactions)
                        .to(ListTransactionsResponse::class.java)
                        .pagination(
                            Pagination.builder()
                                .method(Pagination.Method.NEXTKEY)
                                .nextkey("nextKey")
                                .build()
                        )
                        .merge(cardShinhancardTransactionsMerge)
                        .build()
                )
                // 체크 해외사용내역 조회 SHC_HPG01031
                .with(
                    Execution.create()
                        .exchange(ShinhancardApis.card_shinhancard_check_oversea_transactions)
                        .to(ListTransactionsResponse::class.java)
                        .pagination(
                            Pagination.builder()
                                .method(Pagination.Method.NEXTKEY)
                                .nextkey("nextKey")
                                .build()
                        )
                        .merge(cardShinhancardTransactionsMerge)
                        .build()
                )
                .merge(cardShinhancardTransactionsMerge)
                .build()
    }
}
