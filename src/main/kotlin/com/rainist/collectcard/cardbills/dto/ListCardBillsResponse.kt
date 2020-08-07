package com.rainist.collectcard.cardbills.dto

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.google.protobuf.StringValue
import com.rainist.collectcard.common.exception.CollectcardException
import java.text.SimpleDateFormat
import org.springframework.util.StringUtils

data class ListCardBillsResponse(
    var dataHeader: ListCardBillsResponseDataHeader? = null,
    var dataBody: ListCardBillsResponseDataBody? = null
)

data class ListCardBillsResponseDataHeader(
    var resultCode: String?,
    var resultMessage: String?,
    var successCode: String?
)

data class ListCardBillsResponseDataBody(
    // 결제 예정 상세 내역
    var cardBills: MutableList<CardBill>? = null,
    // 다음 조회 key
    var nextKey: String = ""
)

fun ListCardBillsResponse.toListCardBillsResponseProto(): CollectcardProto.ListCardBillsResponse {
    // TODO 박두상 쉐도잉을 하면서 하나씩 맞춰봐야 할 것 같습니다. 추후 null 해당부분에서 고려하지않게 미리 제거하여 받는 방법도 구성이 필요할 것 같습니다.
    return this.dataBody?.cardBills?.map { cardBill ->
        CollectcardProto.CardBill.newBuilder()
            .setDueDate(SimpleDateFormat("yyyy-MM-dd").format(SimpleDateFormat("yyyyMMdd").parse(cardBill.paymentDay)))
            .setCurrency("KRW") // TODO 3. iso 4217??  받아오는 값인지 의미잇는
            .setBillType(cardBill.billNumber?.let { StringValue.of(it) } ?: StringValue.getDefaultInstance())
            .setLinkedAccount(
                CollectcardProto.BankAccountSummary.newBuilder()
                    .setNumber(cardBill.paymentAccountNumber) // 계좌 번호
                    .setBankCode(StringValue.of(cardBill.paymentBankId)) // 은행 번호
                    .build()
            ) // 청구 계좌
            .addAllTransactions( // 청구 내역
                cardBill.transactions?.map { cardBillTransaction ->

                    // diff를 위해서 Builder 선언
                    val cardSummaryBuilder = CollectcardProto.CardSummary.newBuilder()
                    cardBillTransaction.cardNumber?.let { cardSummaryBuilder.number = cardBillTransaction.cardNumber }
                    cardBillTransaction.cardName?.let { cardSummaryBuilder.name = StringValue.of(cardBillTransaction.cardName) }

                    CollectcardProto.CardBillTransaction.newBuilder()
                        .setTransactedAt(SimpleDateFormat("yyyy-MM-dd").format(SimpleDateFormat("yyyyMMdd").parse(cardBillTransaction.approvalDay)))
                        .setAmount(cardBillTransaction.amount?.toDouble() ?: 0.0)
                        .setCurrency(
                            if (StringUtils.isEmpty(cardBillTransaction.currencyCode)) { // 빈값으로 존재하는 value가 diff매칭이 안되는부분 수정
                                "KRW"
                            } else {
                                cardBillTransaction.currencyCode
                            }
                        )
                        .setInstallment(false) // TODO 박두상 향후 해당부분 구현 필요
                        .setFee(cardBillTransaction.serviceChargeAmount?.toDouble() ?: 0.0)
                        .setCard(cardSummaryBuilder.build())
                        .setStore(CollectcardProto.AffiliatedStoreSummary.newBuilder()
                            .setName(cardBillTransaction.storeName)
                            .build()
                        )
                        .build()
                }
                    ?.toMutableList()
                    ?: mutableListOf<CollectcardProto.CardBillTransaction>()
            )
            // 2. 총 청구 금액 billingAmount로 하면 좋겠으나 청구서 이용내역시 겹치는 부분때문에 우선 수동계산으로 변
            .setTotalAmount(
                cardBill.transactions?.map {
                    it.amount?.toDouble() ?: 0.0
                }
                    ?.sum()
                    ?: 0.0
            )
            .build()
    }
        .let {
            CollectcardProto.ListCardBillsResponse
                .newBuilder()
                .addAllData(it ?: mutableListOf())
                .build()
        }
}

fun <T : Any> safeValue(input: T?): T {
    return input?.let { input } ?: throw CollectcardException("parameter is null") // TODO 임시 코드 NULL CHECK를 하는부분을 공통으로 빼거나 하는 처리로직 필요.
}
