package com.rainist.collectcard.cardcreditlimit

import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitResponse
import com.rainist.collectcard.common.dto.CollectExecutionContext
import java.time.LocalDateTime

interface CardCreditLimitService {
    fun cardCreditLimit(executionContext: CollectExecutionContext, now: LocalDateTime): CreditLimitResponse
}
