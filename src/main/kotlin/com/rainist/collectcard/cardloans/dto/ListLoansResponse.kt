package com.rainist.collectcard.cardloans.dto

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.common.exception.ValidationException
import java.math.BigDecimal

data class ListLoansResponse(
    var dataHeader: ListLoansResponseDataHeader? = null,
    var dataBody: ListLoansResponseDataBody? = null
)

data class ListLoansResponseDataHeader(
    var successCode: String? = null,
    var resultCode: String? = null,
    var resultMessage: String? = null
)

data class ListLoansResponseDataBody(
    var loans: MutableList<Loan>? = null,
    var nextKey: String? = null
)

fun ListLoansResponse.toListCardLoansResponseProto(): CollectcardProto.ListCardLoansResponse {
    return let {
        this.dataBody?.loans?.map {
            CollectcardProto.CardLoan.newBuilder()
                .setNumber(it.loanNumber)
                .setName(it.loanName)
                .setPrincipal(it.loanAmount?.setScale(2)?.multiply(BigDecimal(100L))?.toLong() ?: throw ValidationException("대출 금액이 없습니다"))
                .setCurrency("KRW") // TODO 예상국 기존 로직 확인 통화코드를 안줌
                .setLatestBalance(it.remainingAmount?.setScale(2)?.multiply(BigDecimal(100L))?.toLong() ?: throw ValidationException("대출 잔액이 없습니다"))
                .setInterestRate2F(it.interestRate?.setScale(2)?.toLong() ?: throw ValidationException("이자율이 없습니다"))
                // .setCreatedAtMs() // TODO 예상국 무슨값?
                // .setUpdatedAtMs() // TODO 예상국 무슨값?
                // .setOverdueStatus() // TODO 예상국 무슨값?
                .build()
        }
        ?.toMutableList()
        ?: mutableListOf()
    }
    .let {
        CollectcardProto.ListCardLoansResponse
            .newBuilder()
            .addAllCardLoans(it)
            .build()
    }
}
