package com.rainist.collectcard.cardloans

import com.rainist.collectcard.cardloans.dto.ListLoansResponse
import com.rainist.collectcard.common.dto.SyncRequest

interface CardLoanService {
    fun listCardLoans(syncRequest: SyncRequest): ListLoansResponse
}
