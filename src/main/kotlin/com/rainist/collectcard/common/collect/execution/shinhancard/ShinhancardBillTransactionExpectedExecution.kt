package com.rainist.collectcard.common.collect.execution.shinhancard

import com.rainist.collect.common.api.Pagination
import com.rainist.collect.common.execution.Execution
import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collectcard.cardbills.dto.CardBill
import com.rainist.collectcard.cardbills.dto.ListBillTransactionsResponse
import com.rainist.collectcard.cardbills.dto.ListBillTransactionsResponseDataBody
import com.rainist.collectcard.cardbills.dto.ListCardBillsResponse
import com.rainist.collectcard.cardbills.dto.ListCardBillsResponseDataBody
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.exception.CollectExecutionExceptionHandler
import java.util.function.BiConsumer
import java.util.function.BinaryOperator

class ShinhancardBillTransactionExpectedExecution {

    companion object {
        val mergeBills =
            BinaryOperator { prev: ListCardBillsResponse, next: ListCardBillsResponse ->

                val prevCardBill = prev.dataBody?.cardBills ?: mutableListOf()
                val nextCardBill = next.dataBody?.cardBills ?: mutableListOf()
                prevCardBill.addAll(nextCardBill)

                prev.dataBody = ListCardBillsResponseDataBody(cardBills = prevCardBill, nextKey = next.dataBody?.nextKey)
                prev
            }

        val mergeBillAndBillTransaction =
            BiConsumer { master: CardBill, detail: ListBillTransactionsResponse ->
                val masterTransaction = master.transactions ?: mutableListOf()
                val detailTransaction = detail.dataBody?.billTransactions ?: mutableListOf()

                masterTransaction.addAll(detailTransaction)

                masterTransaction.forEach {
                    // 연회비인 경우 approvalDay가 없음
                    if (it.approvalDay.isNullOrEmpty()) {
                        it.approvalDay = master.paymentDay
                    }
                    it.billNumber = master.billNumber
                    it.billType = master.billType
                }

                master.transactions = masterTransaction
            }

        val mergeBillTransactions =
            BinaryOperator { prev: ListBillTransactionsResponse, next: ListBillTransactionsResponse ->
                val prevTransaction = prev.dataBody?.billTransactions ?: mutableListOf()
                val nextTransaction = next.dataBody?.billTransactions ?: mutableListOf()
                prevTransaction.addAll(nextTransaction)

                prev.dataBody = ListBillTransactionsResponseDataBody(billTransactions = prevTransaction, nextKey = next.dataBody?.nextKey)
                prev
            }

        val mergeBillByBillTransactionExpected =
            BinaryOperator { prev: ListCardBillsResponse, next: ListCardBillsResponse ->
                val prevCardBills = prev.dataBody?.cardBills ?: mutableListOf()
                val nextCardBills = next.dataBody?.cardBills ?: mutableListOf()

                prevCardBills.forEach {
                    it.transactions = it.transactions ?: mutableListOf()
                }

                nextCardBills.forEach {
                    it.transactions = it.transactions ?: mutableListOf()
                }

                nextCardBills.forEach { nextCardBill ->
                    val billerNumber = nextCardBill.billNumber

                    prevCardBills.forEach { prevCardBill ->
                        if (prevCardBill.billNumber == billerNumber) {
                            prevCardBill.transactions?.addAll(nextCardBill.transactions ?: mutableListOf())
                        }
                    }
                }

                prevCardBills.sortByDescending { it -> it.paymentDay }

                prev.dataBody = ListCardBillsResponseDataBody(cardBills = prevCardBills, nextKey = next.dataBody?.nextKey)
                prev
            }

        val cardShinhancardBillTransactionExpected =
            Execution.create()
                // 카드_[EXT] 결제예정금액총괄 SHC_HPG01096_EXT
                .exchange(ShinhancardApis.card_shinhancard_list_user_card_bills_expected)
                .to(ListCardBillsResponse::class.java)
                .exceptionally { executionContext: ExecutionContext, throwable: Throwable ->
                    CollectExecutionExceptionHandler.handle(
                        executionContext,
                        Organization.shinhancard.name,
                        "cardShinhancardBillTransactionExpected",
                        ShinhancardApis.card_shinhancard_list_user_card_bills_expected.id,
                        throwable
                    )
                }
                .paging(
                    Pagination.builder()
                        .method(Pagination.Method.NEXTKEY)
                        .nextkey(".dataBody.nextKey")
                        .merge(mergeBills)
                        .build()
                )
                .fetch { listCardBillResponse ->
                    listCardBillResponse as ListCardBillsResponse
                    listCardBillResponse.dataBody?.cardBills?.iterator() ?: listOf<Any>().iterator()
                }.then(
                    // 결제예정금액(일시불,현금서비스 상세)(SHC_HPG00237)
                    Execution.create()
                        .exchange(ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_lump_sum)
                        .to(ListBillTransactionsResponse::class.java)
                        .exceptionally { executionContext: ExecutionContext, throwable: Throwable ->
                            CollectExecutionExceptionHandler.handle(
                                executionContext,
                                Organization.shinhancard.name,
                                "cardShinhancardBillTransactionExpected",
                                ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_lump_sum.id,
                                throwable
                            )
                        }
                        .paging(
                            Pagination.builder()
                                .method(Pagination.Method.NEXTKEY)
                                .nextkey(".dataBody.nextKey")
                                .merge(mergeBillTransactions)
                                .build()
                        )
                        .build()
                ).merge(mergeBillAndBillTransaction)
                .with(
                    Execution.create()
                        // 카드_[EXT] 결제예정금액총괄 SHC_HPG01096_EXT
                        .exchange(ShinhancardApis.card_shinhancard_list_user_card_bills_expected)
                        .to(ListCardBillsResponse::class.java)
                        .exceptionally { executionContext: ExecutionContext, throwable: Throwable ->
                            CollectExecutionExceptionHandler.handle(
                                executionContext,
                                Organization.shinhancard.name,
                                "cardShinhancardBillTransactionExpected",
                                ShinhancardApis.card_shinhancard_list_user_card_bills_expected.id,
                                throwable
                            )
                        }
                        .paging(
                            Pagination.builder()
                                .method(Pagination.Method.NEXTKEY)
                                .nextkey(".dataBody.nextKey")
                                .merge(mergeBills)
                                .build()
                        )
                        .fetch { listCardBillResponse ->
                            listCardBillResponse as ListCardBillsResponse
                            listCardBillResponse.dataBody?.cardBills?.iterator() ?: listOf<Any>().iterator()
                        }.then(
                            // (할부) 결제예정금액(할부, 론 상세)(SHC_HPG00238)
                            Execution.create()
                                .exchange(ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_installment)
                                .to(ListBillTransactionsResponse::class.java)
                                .exceptionally { executionContext: ExecutionContext, throwable: Throwable ->
                                    CollectExecutionExceptionHandler.handle(
                                        executionContext,
                                        Organization.shinhancard.name,
                                        "cardShinhancardBillTransactionExpected",
                                        ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_installment.id,
                                        throwable
                                    )
                                }
                                .paging(
                                    Pagination.builder()
                                        .method(Pagination.Method.NEXTKEY)
                                        .nextkey(".dataBody.nextKey")
                                        .merge(mergeBillTransactions)
                                        .build()
                                )
                                .build()
                        ).merge(mergeBillAndBillTransaction)
                        .build()
                )
                .merge(mergeBillByBillTransactionExpected)
                .build()
    }
}
