package com.rainist.collectcard.common.collect.execution

import com.rainist.collect.common.api.Pagination
import com.rainist.collect.common.execution.Execution
import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collectcard.card.dto.ListCardsResponse
import com.rainist.collectcard.cardbills.dto.CardBill
import com.rainist.collectcard.cardbills.dto.ListBillTransactionsResponse
import com.rainist.collectcard.cardbills.dto.ListCardBillsResponse
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitResponse
import com.rainist.collectcard.cardloans.dto.ListLoansResponse
import com.rainist.collectcard.cardloans.dto.Loan
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponseDataBody
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.enums.ResultCode
import com.rainist.collectcard.common.exception.CollectExecutionExceptionHandler
import com.rainist.collectcard.userinfo.dto.UserInfoResponse
import com.rainist.common.util.DateTimeUtil
import java.util.function.BiConsumer
import java.util.function.BinaryOperator
import java.util.regex.Pattern
import org.apache.commons.lang3.StringUtils

class ShinhancardExecutions {

    companion object {
        val organizationIdShinhancard = "shinhancard"

        // 대출 상세
        val mergeLoansDetail =
            BiConsumer { master: Loan, detail: Loan ->
                master.loanId = detail.loanId
                master.loanNumber = detail.loanNumber
                master.loanAmount = detail.loanAmount
                master.issuedDay = detail.issuedDay
                master.expirationDay = detail.expirationDay
                master.interestRate = detail.interestRate
                master.repaymentMethod = detail.repaymentMethod
                master.remainingAmount = detail.remainingAmount
            }

        // 대출 정보
        val mergeLoans =
            BinaryOperator { prevListLoanResponse: ListLoansResponse, nextListLoanResponse: ListLoansResponse ->
                nextListLoanResponse?.dataBody?.loans?.addAll(
                    0, prevListLoanResponse?.dataBody?.loans ?: mutableListOf()
                )
                nextListLoanResponse
            }

        val mergeBills =
            BinaryOperator { prev: ListCardBillsResponse, next: ListCardBillsResponse ->
                next?.dataBody?.cardBills?.addAll(
                    0, prev?.dataBody?.cardBills ?: mutableListOf()
                )
                next
            }

        val mergeTransactions =
            BinaryOperator { prev: ListBillTransactionsResponse, next: ListBillTransactionsResponse ->
                next?.dataBody?.billTransactions?.addAll(0, prev?.dataBody?.billTransactions ?: mutableListOf())
                    ?: kotlin.run { next?.dataBody?.billTransactions = mutableListOf() }
                next
            }

        val mergeBillByBillTransactionExpected =
            BinaryOperator { prev: ListCardBillsResponse, next: ListCardBillsResponse ->
                next?.dataBody?.cardBills?.forEachIndexed { idx, cardBill ->
                    if (cardBill.transactions == null) {
                        cardBill.transactions = mutableListOf()
                    }
                    prev?.dataBody?.cardBills?.get(idx)?.transactions?.let {
                        cardBill.transactions?.addAll(it)
                    }
                }
                next?.dataBody?.cardBills?.sortByDescending { it -> it.paymentDay }
                next
            }

        val mergeBillByBills =
            BinaryOperator { prev: ListCardBillsResponse, next: ListCardBillsResponse ->
                next?.dataBody?.cardBills?.addAll(
                    0, prev?.dataBody?.cardBills ?: mutableListOf()
                )

                // transactions 없는 리스트 제거
                next?.dataBody?.cardBills = next?.dataBody?.cardBills?.filter { it.transactions != null }?.toMutableList() ?: mutableListOf()

                // cardNumber 한글 제거 처리 진행
                val pattern = Pattern.compile("[0-9]*\$")
                next.dataBody?.cardBills?.forEach { cardBill ->
                    cardBill.transactions?.forEach { cardBillTransaction ->
                        cardBillTransaction.cardNumber?.let { cardNumber ->
                            val matcher = pattern.matcher(cardNumber)
                            if (matcher.find()) {
                                cardBillTransaction.cardNumber = matcher.group(0)
                            }
                        }
                    }
                }

                next
            }

        val mergeBillTransaction =
            BiConsumer { master: CardBill, detail: ListBillTransactionsResponse ->
                if (master.transactions == null) {
                    master.transactions = mutableListOf()
                }
                master.transactions?.addAll(detail?.dataBody?.billTransactions ?: mutableListOf())

                // 연회비인 경우 approvalDay가 없음
                master.transactions?.forEach {
                    if (StringUtils.isEmpty(it.approvalDay)) {
                        it.approvalDay = master.paymentDay
                    }
                }
            }

        val mergeCards =
            BinaryOperator { listCardsResponse1: ListCardsResponse, listCardsResponse2: ListCardsResponse ->
                listCardsResponse1.resultCodes.add(listCardsResponse2.dataHeader?.resultCode ?: ResultCode.UNKNOWN)

                listCardsResponse1.dataBody?.cards?.addAll(
                    listCardsResponse2.dataBody?.cards ?: mutableListOf()
                )

                listCardsResponse1
            }

        /* 카드 이용내역 merge */
        val cardShinhancardTransactionsMerge =

            BinaryOperator { prev: ListTransactionsResponse, next: ListTransactionsResponse ->
                prev.resultCodes.add(next.dataHeader?.resultCode ?: ResultCode.UNKNOWN)

                val prevTransactions = prev.dataBody?.transactions ?: mutableListOf()
                val nextTransactions = next.dataBody?.transactions ?: mutableListOf()
                prevTransactions.addAll(nextTransactions)

                prev.dataBody = ListTransactionsResponseDataBody(transactions = prevTransactions, nextKey = next.dataBody?.nextKey)
                prev
            }

        val cardShinhancardCards =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_cards)
                .to(ListCardsResponse::class.java)
                .exceptionally { throwable: Throwable ->
                    CollectExecutionExceptionHandler.handle(
                        organizationIdShinhancard,
                        "cardShinhancardCards",
                        ShinhancardApis.card_shinhancard_cards.id,
                        throwable
                    )
                }
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
                .exceptionally { throwable: Throwable ->
                    CollectExecutionExceptionHandler.handle(
                        organizationIdShinhancard,
                        "cardShinhancardTransactions",
                        ShinhancardApis.card_shinhancard_credit_domestic_transactions.id,
                        throwable
                    )
                }
                .paging(
                    Pagination.builder()
                        .method(Pagination.Method.NEXTKEY)
                        .nextkey(".dataBody.nextKey")
                        .merge(cardShinhancardTransactionsMerge)
                        .build()
                )
                // 신용 해외사용내역조회-일시불조회 SHC_HPG01612
                .with(
                    Execution.create()
                        .exchange(ShinhancardApis.card_shinhancard_credit_oversea_transactions)
                        .to(ListTransactionsResponse::class.java)
                        .exceptionally { throwable: Throwable ->
                            CollectExecutionExceptionHandler.handle(
                                organizationIdShinhancard,
                                "cardShinhancardTransactions",
                                ShinhancardApis.card_shinhancard_credit_oversea_transactions.id,
                                throwable
                            )
                        }
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
                        .exceptionally { throwable: Throwable ->
                            CollectExecutionExceptionHandler.handle(
                                organizationIdShinhancard,
                                "cardShinhancardTransactions",
                                ShinhancardApis.card_shinhancard_check_domestic_transactions.id,
                                throwable
                            )
                        }
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
                        .exceptionally { throwable: Throwable ->
                            CollectExecutionExceptionHandler.handle(
                                organizationIdShinhancard,
                                "cardShinhancardTransactions",
                                ShinhancardApis.card_shinhancard_check_oversea_transactions.id,
                                throwable
                            )
                        }
                        .paging(
                            Pagination.builder()
                                .method(Pagination.Method.NEXTKEY)
                                .nextkey(".dataBody.nextKey")
                                .merge(cardShinhancardTransactionsMerge)
                                .build()
                        ).build()
                )
                .merge(cardShinhancardTransactionsMerge)
                .build()

        val cardShinhancardBillTransactionExpected =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_list_user_card_bills_expected)
                .to(ListCardBillsResponse::class.java)
                .exceptionally { throwable: Throwable ->
                    CollectExecutionExceptionHandler.handle(
                        organizationIdShinhancard,
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
                    listCardBillResponse?.dataBody?.cardBills?.iterator()
                }.then(
                    Execution.create()
                        .exchange(ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_lump_sum)
                        .to(ListBillTransactionsResponse::class.java)
                        .exceptionally { throwable: Throwable ->
                            CollectExecutionExceptionHandler.handle(
                                organizationIdShinhancard,
                                "cardShinhancardBillTransactionExpected",
                                ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_lump_sum.id,
                                throwable
                            )
                        }
                        .paging(
                            Pagination.builder()
                                .method(Pagination.Method.NEXTKEY)
                                .nextkey(".dataBody.nextKey")
                                .merge(mergeTransactions)
                                .build()
                        )
                        .build()
                ).merge(mergeBillTransaction)
                .with(
                    Execution.create()
                        .exchange(ShinhancardApis.card_shinhancard_list_user_card_bills_expected)
                        .to(ListCardBillsResponse::class.java)
                        .exceptionally { throwable: Throwable ->
                            CollectExecutionExceptionHandler.handle(
                                organizationIdShinhancard,
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
                            listCardBillResponse?.dataBody?.cardBills?.iterator()
                        }.then(
                            Execution.create()
                                .exchange(ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_installment)
                                .to(ListBillTransactionsResponse::class.java)
                                .exceptionally { throwable: Throwable ->
                                    CollectExecutionExceptionHandler.handle(
                                        organizationIdShinhancard,
                                        "cardShinhancardBillTransactionExpected",
                                        ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_installment.id,
                                        throwable
                                    )
                                }
                                .paging(
                                    Pagination.builder()
                                        .method(Pagination.Method.NEXTKEY)
                                        .nextkey(".dataBody.nextKey")
                                        .merge(mergeTransactions)
                                        .build()
                                )
                                .build()
                        ).merge(mergeBillTransaction)
                        .build()
                )
                .merge(mergeBillByBillTransactionExpected)
                .build()

        val cardShinhancardBills =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_check_bills)
                .to(ListCardBillsResponse::class.java)
                .exceptionally { throwable: Throwable ->
                    CollectExecutionExceptionHandler.handle(
                        organizationIdShinhancard,
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

                    listCardBillsResponse?.dataBody?.cardBills?.filter { it -> it.paymentDay!! >= paymentDay }?.iterator()
                        ?: mutableListOf<CardBill>().iterator()
                }
                .then(
                    Execution.create()
                        .exchange(ShinhancardApis.card_shinhancard_check_bill_transactions)
                        .to(ListBillTransactionsResponse::class.java)
                        .exceptionally { throwable: Throwable ->
                            CollectExecutionExceptionHandler.handle(
                                organizationIdShinhancard,
                                "cardShinhancardBills",
                                ShinhancardApis.card_shinhancard_check_bill_transactions.id,
                                throwable
                            )
                        }
                        .paging(
                            Pagination.builder()
                                .method(Pagination.Method.NEXTKEY)
                                .nextkey(".dataBody.nextKey")
                                .merge(mergeTransactions)
                                .build()
                        )
                        .build()
                )
                .merge(mergeBillTransaction)
                .with(
                    Execution.create()
                        .exchange(ShinhancardApis.card_shinhancard_credit_bills)
                        .to(ListCardBillsResponse::class.java)
                        .exceptionally { throwable: Throwable ->
                            CollectExecutionExceptionHandler.handle(
                                organizationIdShinhancard,
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

                            listCardBillsResponse?.dataBody?.cardBills?.filter { it -> it.paymentDay!! >= paymentDay }?.iterator()
                                ?: mutableListOf<CardBill>().iterator()
                        }
                        .then(
                            Execution.create()
                                .exchange(ShinhancardApis.card_shinhancard_credit_bill_transactions)
                                .to(ListBillTransactionsResponse::class.java)
                                .exceptionally { throwable: Throwable ->
                                    CollectExecutionExceptionHandler.handle(
                                        organizationIdShinhancard,
                                        "cardShinhancardBills",
                                        ShinhancardApis.card_shinhancard_credit_bill_transactions.id,
                                        throwable
                                    )
                                }
                                .paging(
                                    Pagination.builder()
                                        .method(Pagination.Method.NEXTKEY)
                                        .nextkey(".dataBody.nextKey")
                                        .merge(mergeTransactions)
                                        .build()
                                )
                                .build()
                        )
                        .merge(mergeBillTransaction)
                        .build()
                )
                .merge(mergeBillByBills)
                .build()

        // 사용자 정보 조회 (SHC_EXT_00001)
        val cardShinhancardUserInfo =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_user_info)
                .to(UserInfoResponse::class.java)
                .exceptionally { throwable: Throwable ->
                    CollectExecutionExceptionHandler.handle(
                        organizationIdShinhancard,
                        "cardShinhancardUserInfo",
                        ShinhancardApis.card_shinhancard_user_info.id,
                        throwable
                    )
                }
                .build()

        // 대출정보 조회
        val cardShinhancardLoan =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_loan_info)
                .to(ListLoansResponse::class.java)
                .exceptionally { throwable: Throwable ->
                    CollectExecutionExceptionHandler.handle(
                        organizationIdShinhancard,
                        "cardShinhancardLoan",
                        ShinhancardApis.card_shinhancard_loan_info.id,
                        throwable
                    )
                }
                .paging(
                    Pagination.builder()
                        .method(Pagination.Method.NEXTKEY)
                        .nextkey(".dataBody.nextKey")
                        .merge(mergeLoans)
                        .build()
                )
                .fetch { listLoansResponse ->
                    listLoansResponse as ListLoansResponse
                    listLoansResponse?.dataBody?.loans?.iterator() ?: listOf<Any>().iterator()
                }
                .then(
                    Execution.create()
                        .exchange(ShinhancardApis.card_shinhancard_loan_detail)
                        .to(Loan::class.java)
                        .exceptionally { throwable: Throwable ->
                            CollectExecutionExceptionHandler.handle(
                                organizationIdShinhancard,
                                "cardShinhancardLoan",
                                ShinhancardApis.card_shinhancard_loan_detail.id,
                                throwable
                            )
                        }
                        .build()
                )
                .merge(mergeLoansDetail)
                .build()

        val cardShinhancardCreditLimit =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_credit_limit)
                .to(CreditLimitResponse::class.java)
                .exceptionally { throwable: Throwable ->
                    CollectExecutionExceptionHandler.handle(
                        organizationIdShinhancard,
                        "cardShinhancardCreditLimit",
                        ShinhancardApis.card_shinhancard_credit_limit.id,
                        throwable
                    )
                }
                .build()
    }
}
