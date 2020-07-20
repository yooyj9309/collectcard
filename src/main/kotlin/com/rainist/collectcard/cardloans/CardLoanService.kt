package com.rainist.collectcard.cardloans

import com.rainist.collectcard.cardloans.dto.ListLoansResponse
import com.rainist.collectcard.common.service.CardOrganization

interface CardLoanService {
    fun listCardLoans(banksaladUserId: String, organization: CardOrganization): ListLoansResponse
}
