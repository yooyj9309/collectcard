package com.rainist.collectcard.cardcreditlimit

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto

interface CardCreditLimitService {
    fun cardCreditLimit(request: CollectcardProto.GetCreditLimitRequest): CollectcardProto.GetCreditLimitResponse
}
