package com.rainist.collectcard.cardcreditlimit.dto

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.collectcard.common.enums.ResultCode

data class CreditLimitResponse(
    var dataHeader: CreditLimitResponseDataHeader? = null,
    var dataBody: CreditLimitResponseDataBody? = null
)

data class CreditLimitResponseDataHeader(
    var resultCode: ResultCode? = null,
    var resultMessage: String? = null
)

data class CreditLimitResponseDataBody(
    var creditLimitInfo: CreditLimit? = null
)

fun CreditLimitResponse.toCreditLimitResponseProto(): CollectcardProto.GetCreditLimitResponse {
    this.dataBody?.creditLimitInfo?.let {

        val cardCreditLimitBuilder = CollectcardProto.CreditLimit.newBuilder()

        if (false == it.onetimePaymentLimit?.isAllZeroAmountOrNull()) {
            cardCreditLimitBuilder.singlePaymentLimit = makeLimitStatusProtoResponse(it.onetimePaymentLimit)
        }

        if (false == it.loanLimit?.isAllZeroAmountOrNull()) {
            cardCreditLimitBuilder.totalLimit = makeLimitStatusProtoResponse(it.loanLimit)
        }

        if (false == it.cardLoanLimit?.isAllZeroAmountOrNull()) {
            cardCreditLimitBuilder.loanLimit = makeLimitStatusProtoResponse(it.cardLoanLimit)
        }

        if (false == it.installmentLimit?.isAllZeroAmountOrNull()) {
            cardCreditLimitBuilder.installmentLimit = makeLimitStatusProtoResponse(it.installmentLimit)
        }

        cardCreditLimitBuilder.build()
    }
    ?.let { cardLimit ->

        val getCreditLimitResponseBuilder = CollectcardProto.GetCreditLimitResponse.newBuilder()

        val isCardLimitEmpty =
            !cardLimit.hasSinglePaymentLimit() &&
            !cardLimit.hasTotalLimit() &&
            !cardLimit.hasLoanLimit() &&
            !cardLimit.hasInstallmentLimit()

        if (!isCardLimitEmpty) {
            getCreditLimitResponseBuilder.data = cardLimit
        }

        return getCreditLimitResponseBuilder.build()
    }
    ?: return CollectcardProto.GetCreditLimitResponse.newBuilder().build()
}

private fun makeLimitStatusProtoResponse(limit: Limit?): CollectcardProto.LimitStatus {
    return CollectcardProto.LimitStatus.newBuilder()
    .setTotalAmount(limit?.totalLimitAmount?.toDouble() ?: 0.0)
    .setRemainedAmount(limit?.remainedAmount?.toDouble() ?: 0.0)
    .setUsedAmount(limit?.usedAmount?.toDouble() ?: 0.0)
    .build()
}
