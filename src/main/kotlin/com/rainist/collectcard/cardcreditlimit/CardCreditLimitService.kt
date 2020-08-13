package com.rainist.collectcard.cardcreditlimit

import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitResponse

interface CardCreditLimitService {
    fun cardCreditLimit(executionContext: ExecutionContext): CreditLimitResponse
}
