package com.rainist.collectcard.cardbills

import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collectcard.cardbills.dto.ListCardBillsResponse

interface CardBillService {
    fun listUserCardBills(executionContext: ExecutionContext, startAt: Long?): ListCardBillsResponse
}
