package com.rainist.collectcard.cardtransactions.dto

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.google.protobuf.Int64Value
import com.google.protobuf.StringValue
import com.rainist.common.exception.ValidationException
import com.rainist.common.util.DateTimeUtil
import java.math.BigDecimal

data class ListTransactionsResponse(
    var dataHeader: ListTransactionsResponseDataHeader? = null,
    var dataBody: ListTransactionsResponseDataBody? = null
)

data class ListTransactionsResponseDataHeader(
    var successCode: String? = null,
    var resultCode: String? = null,
    var resultMessage: String? = null
)

data class ListTransactionsResponseDataBody(
    var transactions: MutableList<CardTransaction>? = null,
    var nextKey: String? = null
)

/**
 * ListTransactionResponse to Proto ListCardTransactionsResponse
 * https://github.com/Rainist/idl/blob/2e22832f1b77911a9c9952dfba0b8f99fcd8717c/protos/apis/v1/collectcard/collectcard.proto#L123
 */
fun ListTransactionsResponse.toListCardsReponseProto(): CollectcardProto.ListCardTransactionsResponse {

    return kotlin.runCatching {
        let {
            this.dataBody?.transactions?.map {

                CollectcardProto.CardTransaction
                    .newBuilder()
                    .setApprovalNumber(it.approvalNumber) // TODO 예상국 무승인 거래 경우 대응하기
                    .setTransactedAtMs(
                        DateTimeUtil.stringToLocalDateTime(it.approvalDay!!, "yyyyMMdd", it.approvalTime!!, "HHmmss")
                            .let { DateTimeUtil.kstLocalDateTimeToEpochMilliSecond(it) }
                    )
                    .setStore(
                        CollectcardProto.AffiliatedStoreSummary
                            .newBuilder()
                            .setName(it.storeName)
                            .setType(StringValue.of(it.storeCategory ?: ""))
                            .build()
                    )
                    .setCard(
                        CollectcardProto.CardSummary
                            .newBuilder()
                            .setNumber(it.cardNumber)
                            .setName(StringValue.of(it.cardName ?: ""))
                            .setCardType(StringValue.of(it.cardType ?: ""))
                            .build()
                    )
                    .setInstallmentMonth(
                        it.installment?.let { Int64Value.of(it.toLong()) }
                    )
                    .setApprovedAmount2F(
                        it.amount?.setScale(2)?.multiply(BigDecimal(100L))?.toLong()
                            ?: throw ValidationException("승인금액이 없습니다")
                    )
                    .setCancelledAmount2F(
                        it.canceledAmount?.setScale(2)?.multiply(BigDecimal(100L))?.toLong()
                            ?.let { Int64Value.of(it) }
                            ?: throw ValidationException("취소금액이 없습니다")
                    )
                    .setCurrency(it.currencyCode ?: "KRW")
                    .setIsOverseas(it.isOverseaUse ?: false)
                    .setIsInstallment(it.isInstallmentPayment ?: false)
                    .build()
            }
            ?. toMutableList()
            ?: mutableListOf()
            }
            .let {
                CollectcardProto.ListCardTransactionsResponse
                    .newBuilder()
                    .addAllCardTransactions(it)
                    .build()
            }
    }
    .getOrThrow()
}
