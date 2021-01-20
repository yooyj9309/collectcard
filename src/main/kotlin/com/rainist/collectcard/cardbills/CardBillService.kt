package com.rainist.collectcard.cardbills

import com.rainist.collectcard.cardbills.dto.ListCardBillsResponse
import com.rainist.collectcard.common.dto.CollectExecutionContext
import java.time.LocalDateTime

interface CardBillService {
    fun listUserCardBills(executionContext: CollectExecutionContext, now: LocalDateTime): ListCardBillsResponse
}
