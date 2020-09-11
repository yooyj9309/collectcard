package com.rainist.collectcard.common.collect.execution.shinhancard

import com.rainist.collect.common.api.Pagination
import com.rainist.collect.common.execution.Execution
import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collectcard.cardloans.dto.ListLoansResponse
import com.rainist.collectcard.cardloans.dto.ListLoansResponseDataBody
import com.rainist.collectcard.cardloans.dto.Loan
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.exception.CollectExecutionExceptionHandler
import java.util.function.BiConsumer
import java.util.function.BinaryOperator

class ShinhancardLoanExecution {

    companion object {

        // 대출 정보
        val mergeLoans =
            BinaryOperator { prev: ListLoansResponse, next: ListLoansResponse ->

                val prevLoans = prev.dataBody?.loans ?: mutableListOf()
                val nextLoans = next.dataBody?.loans ?: mutableListOf()

                prevLoans.addAll(nextLoans)

                prev.dataBody = ListLoansResponseDataBody(loans = prevLoans, nextKey = next.dataBody?.nextKey)
                prev
            }

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
                master.repaymentMethodOrigin = detail.repaymentMethodOrigin
                master.remainingAmount = detail.remainingAmount
            }

        // 대출정보 조회
        val cardShinhancardLoan =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_loan_info)
                .to(ListLoansResponse::class.java)
                .exceptionally { executionContext: ExecutionContext, throwable: Throwable ->
                    CollectExecutionExceptionHandler.handle(
                        executionContext,
                        Organization.shinhancard.name,
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
                    listLoansResponse.dataBody?.loans?.iterator() ?: listOf<Any>().iterator()
                }
                .then(
                    Execution.create()
                        .exchange(ShinhancardApis.card_shinhancard_loan_detail)
                        .to(Loan::class.java)
                        .exceptionally { executionContext: ExecutionContext, throwable: Throwable ->
                            CollectExecutionExceptionHandler.handle(
                                executionContext,
                                Organization.shinhancard.name,
                                "cardShinhancardLoan",
                                ShinhancardApis.card_shinhancard_loan_detail.id,
                                throwable
                            )
                        }
                        .build()
                )
                .merge(mergeLoansDetail)
                .build()
    }
}
