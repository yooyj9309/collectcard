package com.rainist.collectcard.cardloans

import com.rainist.collectcard.cardloans.dto.ListLoansResponse
import com.rainist.collectcard.common.dto.CollectExecutionContext

interface CardLoanService {
    fun listCardLoans(executionContext: CollectExecutionContext): ListLoansResponse
}
