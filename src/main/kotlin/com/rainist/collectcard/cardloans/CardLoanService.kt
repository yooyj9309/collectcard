package com.rainist.collectcard.cardloans

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.collectcard.cardloans.dto.ListLoansRequest
import com.rainist.collectcard.cardloans.dto.ListLoansResponse

interface CardLoanService {
    fun listCardLoans(header: MutableMap<String, String?>, listLoansRequest: ListLoansRequest): ListLoansResponse?
    fun listCardLoans(listCardLoansRequest: CollectcardProto.ListCardLoansRequest): CollectcardProto.ListCardLoansResponse?
}
