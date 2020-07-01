package com.rainist.collectcard.common.collect.execution

import com.rainist.collect.common.dto.Execution
import com.rainist.collect.common.dto.Pagination
import com.rainist.collectcard.card.dto.ListCardsResponse
import com.rainist.collectcard.cardbills.dto.CardBill
import com.rainist.collectcard.cardbills.dto.ListCardBillsResponse
import com.rainist.collectcard.cardbills.dto.ListCardBillsResponseDataBody
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import java.util.function.BinaryOperator

class ShinhancardExecutions {

    companion object {
        val mergeBills =
            BinaryOperator { listCardBillsResponse1: ListCardBillsResponse, listCardBillsResponse2: ListCardBillsResponse ->
                val cardBills = ArrayList<CardBill>()
                cardBills.addAll(listCardBillsResponse1.dataBody?.cardBills ?: ArrayList())
                cardBills.addAll(listCardBillsResponse2.dataBody?.cardBills ?: ArrayList())

                ListCardBillsResponse(
                    listCardBillsResponse1.dataHeader,
                    ListCardBillsResponseDataBody(cardBills, listCardBillsResponse2.dataBody?.nextKey)
                )
            }
        val mergeCards =
            BinaryOperator { listCardsResponse1: ListCardsResponse, listCardsResponse2: ListCardsResponse ->

                listCardsResponse2.dataBody?.cards?.addAll(
                    0,
                    listCardsResponse1.dataBody?.cards ?: mutableListOf()
                )

                listCardsResponse2
            }

        val cardShinhancardTransactionsMerge =
            BinaryOperator { prevTransactions: ListTransactionsResponse, nextTransactions: ListTransactionsResponse ->

                nextTransactions.dataBody?.transactions?.addAll(
                    0, prevTransactions.dataBody?.transactions ?: mutableListOf()
                )

                nextTransactions
            }

        val cardShinhancardCards =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_cards)
                .to(ListCardsResponse::class.java)
                .paging(
                    Pagination.builder()
                        .method(Pagination.Method.NEXTKEY)
                        .nextkey(".dataBody.nextKey")
                        .merge(mergeCards)
                        .build()
                ).build()

        val cardShinhancardTransactions =
            Execution.create()
                // 신용 국내사용내역조회-일시불/할부 SHC_HPG00428
                .exchange(ShinhancardApis.card_shinhancard_credit_domestic_transactions)
                .to(ListTransactionsResponse::class.java)
                .paging(
                    Pagination.builder()
                        .method(Pagination.Method.NEXTKEY)
                        .nextkey(".dataBody.nextKey")
                        .merge(cardShinhancardTransactionsMerge)
                        .build()
                )
                // TODO 주석 풀기
                // 신용 해외사용내역조회-일시불조회 SHC_HPG01612
                /*.with(
                    Execution.create()
                        .exchange(ShinhancardApis.card_shinhancard_credit_oversea_transactions)
                        .to(ListTransactionsResponse::class.java)
                        .paging(
                            Pagination.builder()
                                .method(Pagination.Method.NEXTKEY)
                                .nextkey(".dataBody.nextKey")
                                .merge(cardShinhancardTransactionsMerge)
                                .build()
                        ).build()
                )
                // 체크 국내사용내역 조회 SHC_HPG01030
                .with(
                    Execution.create()
                        .exchange(ShinhancardApis.card_shinhancard_check_domestic_transactions)
                        .to(ListTransactionsResponse::class.java)
                        .paging(
                            Pagination.builder()
                                .method(Pagination.Method.NEXTKEY)
                                .nextkey(".dataBody.nextKey")
                                .merge(cardShinhancardTransactionsMerge)
                                .build()
                        ).build()
                )
                // 체크 해외사용내역 조회 SHC_HPG01031
                .with(
                    Execution.create()
                        .exchange(ShinhancardApis.card_shinhancard_check_oversea_transactions)
                        .to(ListTransactionsResponse::class.java)
                        .paging(
                            Pagination.builder()
                                .method(Pagination.Method.NEXTKEY)
                                .nextkey(".dataBody.nextKey")
                                .merge(cardShinhancardTransactionsMerge)
                                .build()
                        ).build()
                )
                .merge(cardShinhancardTransactionsMerge)*/
                .build()

        val cardShinhancardListUserCardBillsExpected =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_list_user_card_bills_expected)
                .to(ListCardBillsResponse::class.java)
                .paging(
                    Pagination.builder()
                        .method(Pagination.Method.NEXTKEY)
                        .nextkey(".dataBody.nextKey")
                        .merge(mergeBills)
                        .build()
                ).build()

        val cardShinhancardBills =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_check_bills)
                .to(ListCardBillsResponse::class.java)
                .paging(
                    Pagination.builder()
                        .method(Pagination.Method.NEXTKEY)
                        .nextkey(".dataBody.nextKey")
                        .merge(mergeBills)
                        .build()
                )
                .with(
                    Execution.create()
                        .exchange(ShinhancardApis.card_shinhancard_credit_bills)
                        .to(ListCardBillsResponse::class.java)
                        .paging(
                            Pagination.builder()
                                .method(Pagination.Method.NEXTKEY)
                                .nextkey(".dataBody.nextKey")
                                .merge(mergeBills)
                                .build()
                        ).build()
                )
                .with(cardShinhancardListUserCardBillsExpected)
                .merge(mergeBills)
                .build()
    }
}
