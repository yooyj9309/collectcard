package com.rainist.collectcard.cardloans

import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collectcard.cardloans.dto.ListLoansResponse

interface CardLoanService {
    fun listCardLoans(executionContext: ExecutionContext): ListLoansResponse
}
