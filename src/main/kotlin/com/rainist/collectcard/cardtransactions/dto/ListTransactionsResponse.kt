package com.rainist.collectcard.cardtransactions.dto

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.google.protobuf.DoubleValue
import com.google.protobuf.Int32Value
import com.google.protobuf.StringValue
import com.rainist.common.exception.ValidationException
import com.rainist.common.util.DateTimeUtil

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
            this.dataBody?.transactions?.map { transaction ->

                CollectcardProto.CardTransaction
                    .newBuilder()
                    .setApprovalNumber(transaction.approvalNumber) // TODO 예상국 무승인 거래 경우 대응하기
                    .setTransactedAt(
                        DateTimeUtil.stringToLocalDateTime(transaction.approvalDay!!, "yyyyMMdd", transaction.approvalTime!!, "HHmmss")
                            .let { DateTimeUtil.kstLocalDateTimeToEpochMilliSecond(it) }.toInt()
                    )
                    .setStore(
                        CollectcardProto.AffiliatedStoreSummary
                            .newBuilder()
                            .setName(transaction.storeName)
                            .setType(StringValue.of(transaction.storeCategory ?: ""))
                            .build()
                    )
                    .setCard(
                        CollectcardProto.CardSummary
                            .newBuilder()
                            .setNumber(transaction.cardNumber)
                            .setName(StringValue.of(transaction.cardName ?: ""))
                            .setType(StringValue.of(transaction.cardType ?: ""))
                            .build()
                    )
                    .setInstallmentMonth(
                        transaction.installment?.let { Int32Value.of(it) }
                    )
                    .setApprovedAmount(
                        transaction.amount?.toDouble() ?: throw ValidationException("승인금액이 없습니다")
                    )
                    .setCancelledAmount(
                        transaction.canceledAmount?.toDouble()
                            ?.let { DoubleValue.of(it) }
                            ?: throw ValidationException("취소금액이 없습니다")
                    )
                    .setCurrency(transaction.currencyCode ?: "KRW")
                    .setForeign(transaction.isOverseaUse ?: false)
                    .setInstallment(transaction.isInstallmentPayment ?: false)
                    .build()
            }
            ?. toMutableList()
            ?: mutableListOf()
            }
            .let {
                CollectcardProto.ListCardTransactionsResponse
                    .newBuilder()
                    .addAllData(it)
                    .build()
            }
    }
    .getOrThrow()
}
