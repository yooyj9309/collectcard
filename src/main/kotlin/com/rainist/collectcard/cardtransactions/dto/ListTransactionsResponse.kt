package com.rainist.collectcard.cardtransactions.dto

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.google.protobuf.DoubleValue
import com.google.protobuf.Int32Value
import com.google.protobuf.StringValue
import com.rainist.collectcard.cardtransactions.util.CardTransactionUtil
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
fun ListTransactionsResponse.toListCardsTransactionResponseProto(): CollectcardProto.ListCardTransactionsResponse {

    return kotlin.runCatching {

        let {
            // TODO Diff 종료후 삭제
            it.sortListTransactionsResponseByDesc()

            this.dataBody?.transactions?.map { transaction ->
                // 카드내역
                val cardTransactionBuilder = CollectcardProto.CardTransaction.newBuilder()

                // 승인번호 (무승인인 경우 null )
                transaction.approvalNumber?.let { cardTransactionBuilder.approvalNumber = (it) }

                // 승인시간
                cardTransactionBuilder.transactedAt = transaction.approvalDateTime().let { DateTimeUtil.kstLocalDateTimeToEpochMilliSecond(it) }.toInt()

                // 가맹점 정보
                val affiliatedStoreSummaryBuilder = CollectcardProto.AffiliatedStoreSummary.newBuilder()

                // 가맹점 이름
                transaction.storeName?.let { affiliatedStoreSummaryBuilder.name = it }

                // 가맹점 업종
                transaction.storeCategory?.let { affiliatedStoreSummaryBuilder.type = StringValue.of(it) }

                // 가맹점 전화번호
                transaction.storeNumber?.let { affiliatedStoreSummaryBuilder.telephoneNumber = StringValue.of(it) }

                // 가맹정보 end
                cardTransactionBuilder.store = affiliatedStoreSummaryBuilder.build()

                // 카드정보
                val cardSummaryBuilder = CollectcardProto.CardSummary.newBuilder()

                // 카드번호
                transaction.cardNumber?.let { cardSummaryBuilder.number = it }

                // 카드이름
                transaction.cardName?.let { cardSummaryBuilder.name = StringValue.of(it) }

                // 카드타입
                cardSummaryBuilder.type = StringValue.of(transaction.cardType.name)

                // 카드정보 end
                cardTransactionBuilder.card = cardSummaryBuilder.build()

                // 할부여부
                transaction.isInstallmentPayment?.let { cardTransactionBuilder.installment = it }

                // 할부개월수 0 인경우 null 로 표시 ( 0 으로 넣을 경우 앱에서 할부개월 0 으로 표시 될껄?)
                val installment = transaction.installment ?: 0
                if (installment > 0) {
                    cardTransactionBuilder.installmentMonth = Int32Value.of(installment)
                }

                // 승인금액
                transaction.amount?.let { cardTransactionBuilder.approvedAmount = it.toDouble() }

                // 취소금액
                transaction.canceledAmount?.let { cardTransactionBuilder.cancelledAmount = DoubleValue.of(it.toDouble()) }

                // 통화코드
                cardTransactionBuilder.currency = CardTransactionUtil.currencyCodeMap[transaction.currencyCode] ?: transaction.currencyCode

                // 해외사용여부
                transaction.isOverseaUse?.let { cardTransactionBuilder.foreign = it }

                // 카드내역 end
                cardTransactionBuilder.build()
            }
            ?.toMutableList()
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

// TODO Diff 끝나면 없애기
fun ListTransactionsResponse.sortListTransactionsResponseByDesc() {
    this.dataBody?.transactions?.sortByDescending { it.approvalDay + it.approvalTime }
}
