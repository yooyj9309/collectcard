package com.rainist.collectcard.cardcreditlimit.dto

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.collectcard.card.CardsException
import java.math.BigDecimal

data class CreditLimitResponse(
    var dataHeader: CreditLimitResponseDataHeader?,
    var dataBody: CreditLimitResponseDataBody?
)

data class CreditLimitResponseDataHeader(
    var resultCode: String? = null,
    var resultMessage: String? = null
)

data class CreditLimitResponseDataBody(
    var creditlimitInfo: CreditLimit? = null
)

fun CreditLimitResponse.toCreditLimitResponseProto(): CollectcardProto.GetCreditLimitResponse {
    this.dataBody?.creditlimitInfo?.let {
        CollectcardProto.CreditLimit.newBuilder()
            .setSinglePaymentLimit(
                makeLimitStatusProtoResponse(it.singleTotalAmount, it.singleRemainedAmount, it.singleUsedAmount)
            ).setTotalLimit(
                makeLimitStatusProtoResponse(it.totalAmount, it.remainedAmount, it.usedAmount)
            ).setLoanLimit(
                makeLimitStatusProtoResponse(it.loanTotalAmount, it.loanRemainedAmount, it.loanUsedAmount)
            ).setInstallmentLimit(
                makeLimitStatusProtoResponse(it.installmentTotalAmount, it.installmentRemainedAmount, it.installmentUsedAmount)
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

private fun makeLimitStatusProtoResponse(totalAmount: BigDecimal, remainedAmount: BigDecimal, usedAmount: BigDecimal): CollectcardProto.LimitStatus {
    return CollectcardProto.LimitStatus.newBuilder()
        .setTotalAmount(totalAmount.toDouble())
        .setRemainedAmount(remainedAmount.toDouble())
        .setUsedAmount(usedAmount.toDouble())
        .build()
}
