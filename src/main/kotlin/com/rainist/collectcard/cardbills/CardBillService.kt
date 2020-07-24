package com.rainist.collectcard.cardbills

import com.rainist.collectcard.cardbills.dto.ListCardBillsResponse
import com.rainist.collectcard.common.service.CardOrganization

interface CardBillService {
    fun listUserCardBills(banksaladUserId: String, organization: CardOrganization, startAt: Long?): ListCardBillsResponse
}
