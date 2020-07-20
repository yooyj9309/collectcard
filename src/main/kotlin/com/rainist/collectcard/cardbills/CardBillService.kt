package com.rainist.collectcard.cardbills

import com.rainist.collectcard.cardbills.dto.ListCardBillsResponse

interface CardBillService {
    fun listUserCardBills(banksaladUerId: String, organizationId: String, startAt: Long?): ListCardBillsResponse
}
