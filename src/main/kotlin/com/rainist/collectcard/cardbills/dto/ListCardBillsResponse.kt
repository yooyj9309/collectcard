package com.rainist.collectcard.cardbills.dto

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.google.protobuf.DoubleValue
import com.google.protobuf.Int32Value
import com.google.protobuf.StringValue
import com.rainist.collectcard.common.enums.ResultCode
import java.text.SimpleDateFormat
import org.springframework.util.StringUtils

data class ListCardBillsResponse(
    var resultCodes: MutableList<ResultCode> = mutableListOf(),
    var dataHeader: ListCardBillsResponseDataHeader? = null,
    var dataBody: ListCardBillsResponseDataBody? = null
)

data class ListCardBillsResponseDataHeader(
    var resultCode: ResultCode?,
    var resultMessage: String?,
    var successCode: String?
)

data class ListCardBillsResponseDataBody(
    // 결제 예정 상세 내역
    var cardBills: MutableList<CardBill>? = null,
    // 다음 조회 key
    var nextKey: String? = ""
)

fun ListCardBillsResponse.toListCardBillsResponseProto(): CollectcardProto.ListCardBillsResponse {
    // TODO 박두상 쉐도잉을 하면서 하나씩 맞춰봐야 할 것 같습니다. 추후 null 해당부분에서 고려하지않게 미리 제거하여 받는 방법도 구성이 필요할 것 같습니다.
    return this.dataBody?.cardBills?.map { cardBill ->
        CollectcardProto.CardBill.newBuilder()
            .setDueDate(SimpleDateFormat("yyyy-MM-dd").format(SimpleDateFormat("yyyyMMdd").parse(cardBill.paymentDay)))
            .setCurrency("KRW")
            .setBillType(cardBill.billType?.let { StringValue.of(it) } ?: StringValue.getDefaultInstance())
            .setLinkedAccount(
                CollectcardProto.BankAccountSummary.newBuilder()
                    .setNumber(cardBill.paymentAccountNumber) // 계좌 번호
                    .setBankCode(StringValue.of(cardBill.paymentBankId)) // 은행 번호
                    .build()
            ) // 청구 계좌
            .addAllTransactions( // 청구 내역
                cardBill.transactions?.map { cardBillTransaction ->

                    // diff를 위해서 Builder 선언
                    val cardTransactionBuilder = CollectcardProto.CardBillTransaction.newBuilder()
                    cardTransactionBuilder.transactedAt = SimpleDateFormat("yyyy-MM-dd").format(SimpleDateFormat("yyyyMMdd").parse(cardBillTransaction.approvalDay))
                    cardTransactionBuilder.amount = cardBillTransaction.billedAmount?.toDouble() ?: 0.0
                    cardTransactionBuilder.currency = if (StringUtils.isEmpty(cardBillTransaction.currencyCode)) "KRW" else cardBillTransaction.currencyCode
                    cardTransactionBuilder.installment = cardBillTransaction.isInstallmentPayment ?: false
                    cardTransactionBuilder.fee = cardBillTransaction.serviceChargeAmount?.toDouble() ?: 0.0

                    cardBillTransaction.installment?.let { cardTransactionBuilder.installmentMonth = Int32Value.of(it) }
                    cardBillTransaction.installmentRound?.let { cardTransactionBuilder.installmentRound = Int32Value.of(it) }
                    cardBillTransaction.discountAmount?.let { cardTransactionBuilder.savedAmount = DoubleValue.of(it.toDouble()) }
                    cardBillTransaction.billedFee?.let { cardTransactionBuilder.fee = it.toDouble() }

                    val cardSummaryBuilder = CollectcardProto.CardSummary.newBuilder()
                    cardBillTransaction.cardNumber?.let { cardSummaryBuilder.number = it }
                    cardBillTransaction.cardName?.let { cardSummaryBuilder.name = StringValue.of(it) }
                    cardBillTransaction.cardType?.let { cardSummaryBuilder.type = StringValue.of(it.jg) }
                    cardTransactionBuilder.card = cardSummaryBuilder.build()

                    cardBillTransaction.storeName?.let {
                        val store = CollectcardProto.AffiliatedStoreSummary.newBuilder()
                        store.name = cardBillTransaction.storeName
                        cardTransactionBuilder.store = store.build()
                    }

                    cardTransactionBuilder.build()
                }
                    ?.toMutableList()
                    ?: mutableListOf<CollectcardProto.CardBillTransaction>()
            )
            .setTotalAmount(cardBill.billingAmount?.toDouble() ?: 0.0)
            .build()
    }
        .let {
            CollectcardProto.ListCardBillsResponse
                .newBuilder()
                .addAllData(it ?: mutableListOf())
                .build()
        }
}
