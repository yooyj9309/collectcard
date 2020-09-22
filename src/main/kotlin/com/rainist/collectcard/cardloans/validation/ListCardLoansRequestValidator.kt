package com.rainist.collectcard.cardloans.validation

import com.github.banksalad.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.common.exception.ValidationException
import com.rainist.common.model.ObjectOf
import com.rainist.common.validator.ValidatorIfs
import org.springframework.stereotype.Service

@Service
class ListCardLoansRequestValidator : ValidatorIfs<CollectcardProto.ListCardLoansRequest> {

    override fun isValid(obj: ObjectOf<CollectcardProto.ListCardLoansRequest>): Boolean {

        if (!obj.data.hasCompanyId()) throw ValidationException("CompanyId는 필수 입니다")

        if (obj.data.userId.isNullOrEmpty()) throw ValidationException("UserId는 필수 입니다")

        return true
    }
}
