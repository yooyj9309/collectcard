package com.rainist.collectcard.cardbills

import com.rainist.collectcard.cardbills.dto.ListCardBillsResponse
import com.rainist.collectcard.common.dto.CollectExecutionContext

interface CardBillService {
    fun listUserCardBills(executionContext: CollectExecutionContext, startAt: Long?): ListCardBillsResponse
}
