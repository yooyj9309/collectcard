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
import com.rainist.common.util.DateTimeUtil
import java.util.function.BiConsumer
import java.util.function.BinaryOperator

class ShinhancardBillExecution {

    companion object {

        val mergeBills =
            BinaryOperator { prev: ListCardBillsResponse, next: ListCardBillsResponse ->

                val prevCardBill = prev.dataBody?.cardBills ?: mutableListOf()
                val nextCardBill = next.dataBody?.cardBills ?: mutableListOf()
                prevCardBill.addAll(nextCardBill)

                prev.dataBody =
                    ListCardBillsResponseDataBody(cardBills = prevCardBill, nextKey = next.dataBody?.nextKey)
                prev
            }

        val mergeBillTransactions =
            BinaryOperator { prev: ListBillTransactionsResponse, next: ListBillTransactionsResponse ->
                val prevTransaction = prev.dataBody?.billTransactions ?: mutableListOf()
                val nextTransaction = next.dataBody?.billTransactions ?: mutableListOf()
                prevTransaction.addAll(nextTransaction)

                prev.dataBody = ListBillTransactionsResponseDataBody(
                    billTransactions = prevTransaction,
                    nextKey = next.dataBody?.nextKey
                )
                prev
            }

        val mergeCheckBillTransaction =
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
                master.billingAmount = master.transactions?.sumBy { it.billedAmount?.toInt() ?: 0 }?.toBigDecimal()
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

        val mergeBillByBills =
            BinaryOperator { prev: ListCardBillsResponse, next: ListCardBillsResponse ->

                var prevCardBills = prev.dataBody?.cardBills ?: mutableListOf()
                val nextCardBills = next.dataBody?.cardBills ?: mutableListOf()
                prevCardBills.addAll(nextCardBills)

                // transactions 없는 리스트 제거
                prevCardBills = prevCardBills.filter { it.transactions != null }.toMutableList()

                prev.dataBody =
                    ListCardBillsResponseDataBody(cardBills = prevCardBills, nextKey = next.dataBody?.nextKey)
                prev
            }

        // card bill
        val cardShinhancardBills =
            Execution.create()
                // 체크카드 월별 청구내역(SHC_HPG01226)
                .exchange(ShinhancardApis.card_shinhancard_check_bills)
                .to(ListCardBillsResponse::class.java)
                .exceptionally { executionContext: ExecutionContext, throwable: Throwable ->
                    CollectExecutionExceptionHandler.handle(
                        executionContext,
                        Organization.shinhancard.name,
                        "cardShinhancardBills",
                        ShinhancardApis.card_shinhancard_check_bills.id,
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
                .fetch { executionContext, listCardBillsResponse ->
                    listCardBillsResponse as ListCardBillsResponse
                    executionContext as ExecutionContext
                    val paymentDay = DateTimeUtil.localDatetimeToString(executionContext.startAt, "yyyyMMdd")

                    listCardBillsResponse.dataBody?.cardBills?.filter { it -> it.paymentDay!! >= paymentDay }
                        ?.iterator()
                        ?: mutableListOf<CardBill>().iterator()
                }
                .then(
                    Execution.create()

                        // 카드_[EXT] (체크) 월별청구내역조회(상세총괄) (SHC_HPG00537)
                        .exchange(ShinhancardApis.card_shinhancard_check_bill_transactions)
                        .to(ListBillTransactionsResponse::class.java)
                        .exceptionally { executionContext: ExecutionContext, throwable: Throwable ->
                            CollectExecutionExceptionHandler.handle(
                                executionContext,
                                Organization.shinhancard.name,
                                "cardShinhancardBills",
                                ShinhancardApis.card_shinhancard_check_bill_transactions.id,
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
                )
                .merge(mergeCheckBillTransaction)
                .with(
                    Execution.create()
                        // 신용카드 월별 청구내역(SHC_HPG00719)
                        .exchange(ShinhancardApis.card_shinhancard_credit_bills)
                        .to(ListCardBillsResponse::class.java)
                        .exceptionally { executionContext: ExecutionContext, throwable: Throwable ->
                            CollectExecutionExceptionHandler.handle(
                                executionContext,
                                Organization.shinhancard.name,
                                "cardShinhancardBills",
                                ShinhancardApis.card_shinhancard_credit_bills.id,
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
                        .fetch { executionContext, listCardBillsResponse ->
                            listCardBillsResponse as ListCardBillsResponse
                            executionContext as ExecutionContext
                            val paymentDay = DateTimeUtil.localDatetimeToString(executionContext.startAt, "yyyyMMdd")

                            listCardBillsResponse.dataBody?.cardBills?.filter { it -> it.paymentDay!! >= paymentDay }
                                ?.iterator()
                                ?: mutableListOf<CardBill>().iterator()
                        }
                        .then(
                            Execution.create()
                                // 카드_[EXT] (신용) 월별청구내역조회(상세총괄) (SHC_HPG00698)
                                .exchange(ShinhancardApis.card_shinhancard_credit_bill_transactions)
                                .to(ListBillTransactionsResponse::class.java)
                                .exceptionally { executionContext: ExecutionContext, throwable: Throwable ->
                                    CollectExecutionExceptionHandler.handle(
                                        executionContext,
                                        Organization.shinhancard.name,
                                        "cardShinhancardBills",
                                        ShinhancardApis.card_shinhancard_credit_bill_transactions.id,
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
                        )
                        .merge(mergeBillAndBillTransaction)
                        .build()
                )
                .merge(mergeBillByBills)
                .build()
    }
}
