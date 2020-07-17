package com.rainist.collectcard.cardloans

import com.rainist.collectcard.cardloans.dto.ListLoansResponse

interface CardLoanService {
    fun listCardLoans(banksaladUserId: String, organizationId: String): ListLoansResponse
}
