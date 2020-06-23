package com.rainist.collectcard.cardtransactions.dto

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto

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
    return let {
        this.dataBody?.transactions
            ?.map {
                CollectcardProto.CardTransaction
                    .newBuilder()
                    // TODO 예상국 proto 채우기
                    /*.setApprovalNumber()
                    .setTransactedAtMs()
                    .setStore(
                        CollectcardProto.AffiliatedStoreSummary
                            .newBuilder()
                            .build()
                    )
                    .setCard(
                        CollectcardProto.CardSummary
                            .newBuilder()
                            .build()
                    )
                    .setInstallmentMonth()
                    .setApprovedAmount2F()
                    .setCancelledAmount2F()
                    .setCurrency()
                    .setIsOverseas()
                    .setIsInstallment()*/
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
