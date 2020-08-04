package com.rainist.collectcard.cardbills

import com.rainist.collectcard.cardbills.dto.ListCardBillsResponse
import com.rainist.collectcard.common.dto.SyncRequest

interface CardBillService {
    fun listUserCardBills(syncRequest: SyncRequest, startAt: Long?): ListCardBillsResponse
}
