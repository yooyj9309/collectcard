package com.rainist.collectcard.cardcreditlimit

import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitResponse
import com.rainist.collectcard.common.service.CardOrganization

interface CardCreditLimitService {
    fun cardCreditLimit(banksaladUserId: String, organization: CardOrganization): CreditLimitResponse
}
