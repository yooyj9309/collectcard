package com.rainist.collectcard.cardcreditlimit

import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitResponse
import com.rainist.collectcard.common.dto.CollectExecutionContext

interface CardCreditLimitService {
    fun cardCreditLimit(executionContext: CollectExecutionContext): CreditLimitResponse
}
