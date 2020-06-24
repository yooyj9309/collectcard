package com.rainist.collectcard.cardbills

import com.rainist.collectcard.cardbills.dto.ListCardBillsRequest
import com.rainist.collectcard.cardbills.dto.ListCardBillsResponse

interface CardBillService {
    fun listUserCardBills(listCardBillsRequest: ListCardBillsRequest): ListCardBillsResponse
}
