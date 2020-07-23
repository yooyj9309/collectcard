package com.rainist.collectcard.cardcreditlimit

import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitResponse
import com.rainist.collectcard.common.dto.SyncRequest

interface CardCreditLimitService {
    fun cardCreditLimit(syncRequest: SyncRequest): CreditLimitResponse
}
