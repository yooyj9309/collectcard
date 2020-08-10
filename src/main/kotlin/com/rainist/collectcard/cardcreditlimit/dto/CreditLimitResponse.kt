package com.rainist.collectcard.cardcreditlimit.dto

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.collectcard.card.CardsException

data class CreditLimitResponse(
    var dataHeader: CreditLimitResponseDataHeader?,
    var dataBody: CreditLimitResponseDataBody?
)

data class CreditLimitResponseDataHeader(
    var resultCode: String? = null,
    var resultMessage: String? = null
)

data class CreditLimitResponseDataBody(
    var creditLimitInfo: CreditLimit? = null
)

fun CreditLimitResponse.toCreditLimitResponseProto(): CollectcardProto.GetCreditLimitResponse {
    this.dataBody?.creditLimitInfo?.let {

        val cardCreditLimitBuilder = CollectcardProto.CreditLimit.newBuilder()

        if (false == it.onetimePaymentLimit?.isAllZeroAmount()) {
            cardCreditLimitBuilder.singlePaymentLimit = makeLimitStatusProtoResponse(it.onetimePaymentLimit)
        }

        if (false == it.loanLimit?.isAllZeroAmount()) {
            cardCreditLimitBuilder.totalLimit = makeLimitStatusProtoResponse(it.loanLimit)
        }

        if (false == it.cardLoanLimit?.isAllZeroAmount()) {
            cardCreditLimitBuilder.loanLimit = makeLimitStatusProtoResponse(it.cardLoanLimit)
        }

        if (false == it.installmentLimit?.isAllZeroAmount()) {
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
    ?: throw CardsException("DataBody data is null, resultCode : ${dataHeader?.resultCode}, resultMessage : ${dataHeader?.resultMessage}")
}

private fun makeLimitStatusProtoResponse(limit: Limit?): CollectcardProto.LimitStatus {
    return CollectcardProto.LimitStatus.newBuilder()
    .setTotalAmount(limit?.totalLimitAmount?.toDouble() ?: 0.0)
    .setRemainedAmount(limit?.remainedAmount?.toDouble() ?: 0.0)
    .setUsedAmount(limit?.usedAmount?.toDouble() ?: 0.0)
    .build()
}
