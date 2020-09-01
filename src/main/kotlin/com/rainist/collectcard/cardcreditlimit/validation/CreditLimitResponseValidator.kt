package com.rainist.collectcard.cardcreditlimit.validation

import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitResponse
import com.rainist.common.model.ObjectOf
import com.rainist.common.validator.ValidatorIfs
import org.springframework.stereotype.Service

@Service
class CreditLimitResponseValidator : ValidatorIfs<CreditLimitResponse> {

    override fun isValid(obj: ObjectOf<CreditLimitResponse>): Boolean {
        if (obj.data.dataBody == null) return false

        if (obj.data.dataBody?.creditLimitInfo == null) return false

        val allDataIsNull = listOf(
            obj.data.dataBody?.creditLimitInfo?.loanLimit?.allDataIsNull() ?: true,
            obj.data.dataBody?.creditLimitInfo?.onetimePaymentLimit?.allDataIsNull() ?: true,
            obj.data.dataBody?.creditLimitInfo?.cardLoanLimit?.allDataIsNull() ?: true,
            obj.data.dataBody?.creditLimitInfo?.creditCardLimit?.allDataIsNull() ?: true,
            obj.data.dataBody?.creditLimitInfo?.debitCardLimit?.allDataIsNull() ?: true,
            obj.data.dataBody?.creditLimitInfo?.cashServiceLimit?.allDataIsNull() ?: true,
            obj.data.dataBody?.creditLimitInfo?.overseaLimit?.allDataIsNull() ?: true
        ).all { it }
        if (allDataIsNull) return false

        return true
    }
}
