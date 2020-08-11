package com.rainist.collectcard.cardloans.dto

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.google.protobuf.StringValue
import com.rainist.common.exception.ValidationException
import com.rainist.common.util.DateTimeUtil

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
            val loanBuilder = CollectcardProto.CardLoan.newBuilder()

            loanBuilder.number = loan.loanNumber
            loanBuilder.name = loan.loanName
            loanBuilder.currency = "KRW"

            loan.loanAmount?.let { loanBuilder.principal = loan.loanAmount?.toInt() ?: throw ValidationException("대출 금액이 없습니다") }
            loan.remainingAmount?.let { loanBuilder.latestBalance = loan.remainingAmount?.toInt() ?: throw ValidationException("대출 잔액이 없습니다") }
            loan.interestRate?.let { loanBuilder.interestRate = loan.interestRate?.toDouble() ?: throw ValidationException("이자율이 없습니다") }

            loan.issuedDay?.let {
                val issuedDate = DateTimeUtil.stringToLocalDate(it, "yyyyMMdd")
                loanBuilder.createdAt = StringValue.of(DateTimeUtil.localDateToString(issuedDate, "yyyy-MM-dd"))
            }
            loan.expirationDay?.let {
                val expiredDate = DateTimeUtil.stringToLocalDate(it, "yyyyMMdd")
                loanBuilder.expiredAt = StringValue.of(DateTimeUtil.localDateToString(expiredDate, "yyyy-MM-dd"))
            }

            loanBuilder.build()
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
