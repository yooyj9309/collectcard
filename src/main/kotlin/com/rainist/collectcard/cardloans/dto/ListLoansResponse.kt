package com.rainist.collectcard.cardloans.dto

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.google.protobuf.StringValue
import com.rainist.common.exception.ValidationException

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
    var nextKey: String = ""
)

fun ListLoansResponse.toListCardLoansResponseProto(): CollectcardProto.ListCardLoansResponse {
    return let {
        this.dataBody?.loans?.map { loan ->
            CollectcardProto.CardLoan.newBuilder()
                .setNumber(loan.loanNumber)
                .setName(loan.loanName)
                .setPrincipal(loan.loanAmount?.toInt() ?: throw ValidationException("대출 금액이 없습니다"))
                .setCurrency("KRW") // TODO 예상국 기존 로직 확인 통화코드를 안줌
                .setLatestBalance(loan.remainingAmount?.toInt() ?: throw ValidationException("대출 잔액이 없습니다"))
                .setInterestRate(loan.interestRate?.toDouble() ?: throw ValidationException("이자율이 없습니다"))
                .setCreatedAt(loan.issuedDate?.let { StringValue.of(it) } ?: StringValue.getDefaultInstance())
                .setExpiredAt(loan.expirationDate?.let { StringValue.of(it) } ?: StringValue.getDefaultInstance())
//                 .setOverdueStatus() // TODO 연체 여부
                .build()
        }
        ?.toMutableList()
        ?: mutableListOf()
    }
    .let {
        CollectcardProto.ListCardLoansResponse
            .newBuilder()
            .addAllData(it)
            .build()
    }
}
