package com.rainist.collectcard.common.collect.execution

import com.rainist.collect.common.api.Pagination
import com.rainist.collect.common.execution.Execution
import com.rainist.collectcard.card.dto.ListCardsResponse
import com.rainist.collectcard.cardbills.dto.CardBill
import com.rainist.collectcard.cardbills.dto.CardBillTransaction
import com.rainist.collectcard.cardbills.dto.ListBillTransactionsResponse
import com.rainist.collectcard.cardbills.dto.ListCardBillsResponse
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitResponse
import com.rainist.collectcard.cardloans.dto.ListLoansResponse
import com.rainist.collectcard.cardloans.dto.Loan
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponseDataBody
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.userinfo.dto.UserInfoResponse
import java.util.function.BiConsumer
import java.util.function.BinaryOperator

class ShinhancardExecutions {

    companion object {

        // 대출 상세
        val mergeLoansDetail =
            BiConsumer { master: Loan, detail: Loan ->
                master.loanId = detail.loanId
                master.loanNumber = detail.loanNumber
                master.loanAmount = detail.loanAmount
                master.issuedDate = detail.issuedDate
                master.expirationDate = detail.expirationDate
                master.interestRate = detail.interestRate
                master.repaymentMethod = detail.repaymentMethod
                master.remainingAmount = detail.remainingAmount
                master.loanCategory = master.loanName
            }

        // 대출 정보
        val mergeLoans =
            BinaryOperator { prevListLoanResponse: ListLoansResponse, nextListLoanResponse: ListLoansResponse ->
                nextListLoanResponse.dataBody?.loans?.addAll(
                    0, prevListLoanResponse.dataBody?.loans ?: mutableListOf()
                )
                nextListLoanResponse
            }

        val mergeBills =
            BinaryOperator { prev: ListCardBillsResponse, next: ListCardBillsResponse ->

                prev.dataBody?.cardBills?.addAll(
                    0,
                    prev.dataBody?.cardBills ?: mutableListOf()
                )

                next
            }

        val mergeBillTransaction =
            BiConsumer { master: CardBill, detail: ListBillTransactionsResponse ->
                master.transactions = detail.dataBody?.billTransactions ?: mutableListOf<CardBillTransaction>()
            }

        val mergeCards =
            BinaryOperator { listCardsResponse1: ListCardsResponse, listCardsResponse2: ListCardsResponse ->

                listCardsResponse2.dataBody?.cards?.addAll(
                    0,
                    listCardsResponse1.dataBody?.cards ?: mutableListOf()
                )

                listCardsResponse2
            }

        // TODO 박두상 파악해본결과, 현재 해당 구조에서는 이전의 에러코드를 전부 덮어버리는 형태. 최소한의 기록을 위해서라도 따로 resultCode, Message를 기록할 방법 고려 (진짜 에러가 있을 수 있으니까)
        val cardShinhancardTransactionsMerge =
            BinaryOperator { prevTransactions: ListTransactionsResponse, nextTransactions: ListTransactionsResponse ->
                var filteredTransactions = prevTransactions.dataBody?.transactions?.filter { it -> it.approvalDay != "" } ?: mutableListOf()
                nextTransactions.dataBody?.transactions?.addAll(
                    0, filteredTransactions
                ) ?: kotlin.run {
                    nextTransactions.dataBody = ListTransactionsResponseDataBody(filteredTransactions.toMutableList(), "")
                }

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
                // 신용 해외사용내역조회-일시불조회 SHC_HPG01612
                .with(
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
                .merge(cardShinhancardTransactionsMerge)
                .build()

        val cardShinhancardBillTransactionExpected =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_list_user_card_bills_expected)
                .to(ListCardBillsResponse::class.java)
                .paging(
                    Pagination.builder()
                        .method(Pagination.Method.NEXTKEY)
                        .nextkey(".dataBody.nextKey")
                        .merge(mergeBills)
                        .build()
                )
                .fetch { listCardBillResponse ->
                    listCardBillResponse as ListCardBillsResponse
                    listCardBillResponse.dataBody?.cardBills?.iterator()
                }.then(
                    Execution.create()
                        .exchange(ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_lump_sum)
                        .to(ListCardBillsResponse::class.java)
                        .build()
                ).merge(mergeBillTransaction)
                .with(
                    Execution.create()
                        .exchange(ShinhancardApis.card_shinhancard_list_user_card_bills_expected)
                        .to(ListCardBillsResponse::class.java)
                        .paging(
                            Pagination.builder()
                                .method(Pagination.Method.NEXTKEY)
                                .nextkey(".dataBody.nextKey")
                                .merge(mergeBills)
                                .build()
                        )
                        .fetch { listCardBillResponse ->
                            listCardBillResponse as ListCardBillsResponse
                            listCardBillResponse.dataBody?.cardBills?.iterator()
                        }.then(
                            Execution.create()
                                .exchange(ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_installment)
                                .to(ListCardBillsResponse::class.java)
                                .build()
                        ).merge(mergeBillTransaction)
                        .build()
                )
                .build()

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
                .fetch { listCardBillsResponse ->
                    listCardBillsResponse as ListCardBillsResponse
                    listCardBillsResponse.dataBody?.cardBills?.iterator() ?: mutableListOf<CardBill>().iterator()
                }
                .then(
                    Execution.create()
                        .exchange(ShinhancardApis.card_shinhancard_check_bill_transactions)
                        .to(ListBillTransactionsResponse::class.java)
                        .paging(
                            Pagination.builder()
                                .method(Pagination.Method.NEXTKEY)
                                .nextkey(".dataBody.nextKey")
                                .merge(mergeBills)
                                .build()
                        )
                        .build()
                )
                .merge(mergeBillTransaction)
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
                        )
                        .fetch { listCardBillsResponse ->
                            listCardBillsResponse as ListCardBillsResponse
                            listCardBillsResponse.dataBody?.cardBills?.iterator() ?: mutableListOf<CardBill>().iterator()
                        }
                        .then(
                            Execution.create()
                                .exchange(ShinhancardApis.card_shinhancard_credit_bill_transactions)
                                .to(ListBillTransactionsResponse::class.java)
                                .paging(
                                    Pagination.builder()
                                        .method(Pagination.Method.NEXTKEY)
                                        .nextkey(".dataBody.nextKey")
                                        .merge(mergeBills)
                                        .build()
                                )
                                .build()
                        )
                        .merge(mergeBillTransaction)
                        .build()
                )
                .merge(mergeBills)
                .build()

        // 사용자 정보 조회 (SHC_EXT_00001)
        val cardShinhancardUserInfo =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_user_info)
                .to(UserInfoResponse::class.java)
                .build()

        // 대출정보 조회
        val cardShinhancardLoan =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_loan_info)
                .to(ListLoansResponse::class.java)
                .paging(
                    Pagination.builder()
                        .method(Pagination.Method.NEXTKEY)
                        .nextkey(".dataBody.nextKey")
                        .merge(mergeLoans)
                        .build()
                )
                .fetch { listLoansResponse ->
                    listLoansResponse as ListLoansResponse
                    listLoansResponse.dataBody?.loans?.iterator() ?: listOf<Any>().iterator()
                }
                .then(
                    Execution.create()
                        .exchange(ShinhancardApis.card_shinhancard_loan_detail)
                        .to(Loan::class.java)
                        .build()
                )
                .merge(mergeLoansDetail)
                .build()

        val cardShinhancardCreditLimit =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_credit_limit)
                .to(CreditLimitResponse::class.java)
                .build()
    }
}
