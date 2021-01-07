package com.rainist.collectcard.cardloans

import com.rainist.collectcard.cardloans.dto.ListLoansResponse
import com.rainist.collectcard.common.dto.CollectExecutionContext
import java.time.LocalDateTime

interface CardLoanService {
    fun listCardLoans(executionContext: CollectExecutionContext, now: LocalDateTime): ListLoansResponse
}
