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
        CollectcardProto.CreditLimit.newBuilder()
            .setSinglePaymentLimit(
                makeLimitStatusProtoResponse(it.onetimePaymentLimit)
            ).setTotalLimit(
                makeLimitStatusProtoResponse(it.loanLimit)
            ).setLoanLimit(
                makeLimitStatusProtoResponse(it.cardLoanLimit)
            ).setInstallmentLimit(
                makeLimitStatusProtoResponse(it.installmentLimit)
            )
    }
        ?.let {
            return CollectcardProto.GetCreditLimitResponse
                .newBuilder()
                .setData(it)
                .build()
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
