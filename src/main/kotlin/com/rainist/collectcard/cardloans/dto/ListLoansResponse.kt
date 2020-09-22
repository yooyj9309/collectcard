package com.rainist.collectcard.cardloans.dto

import com.github.banksalad.idl.apis.v1.collectcard.CollectcardProto
import com.google.protobuf.StringValue
import com.rainist.collectcard.common.enums.ResultCode
import com.rainist.common.util.DateTimeUtil

data class ListLoansResponse(
    var resultCodes: MutableList<ResultCode> = mutableListOf(),
    var dataHeader: ListLoansResponseDataHeader? = null,
    var dataBody: ListLoansResponseDataBody? = null
)

data class ListLoansResponseDataHeader(
    var successCode: String? = null,
    var resultCode: ResultCode? = null,
    var resultMessage: String? = null
)

data class ListLoansResponseDataBody(
    var loans: MutableList<Loan>? = null,
    var nextKey: String ? = ""
)

fun ListLoansResponse.toListCardLoansResponseProto(): CollectcardProto.ListCardLoansResponse {
    return let {
        this.dataBody?.loans?.map { loan ->
            val loanBuilder = CollectcardProto.CardLoan.newBuilder()

            loanBuilder.number = loan.loanNumber
            loanBuilder.name = loan.loanName
            loanBuilder.currency = "KRW"

            loan.loanAmount?.let { loanBuilder.principal = it.toInt() }
            loan.remainingAmount?.let { loanBuilder.latestBalance = it.toInt() }
            loan.interestRate?.let { loanBuilder.interestRate = it.toDouble() }

            if (!loan.issuedDay.isNullOrBlank()) {
                val issuedDate = DateTimeUtil.stringToLocalDate(loan.issuedDay!!, "yyyyMMdd")
                loanBuilder.createdAt = StringValue.of(DateTimeUtil.localDateToString(issuedDate, "yyyy-MM-dd"))
            }
            if (!loan.expirationDay.isNullOrBlank()) {
                val expiredDate = DateTimeUtil.stringToLocalDate(loan.expirationDay!!, "yyyyMMdd")
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
