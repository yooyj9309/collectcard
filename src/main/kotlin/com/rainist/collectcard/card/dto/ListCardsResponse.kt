package com.rainist.collectcard.card.dto

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.google.protobuf.BoolValue
import com.google.protobuf.Int64Value
import com.google.protobuf.StringValue

data class ListCardsResponse(
    var dataHeader: ListCardsResponseDataHeader?,
    var dataBody: ListCardsResponseDataBody?
)

data class ListCardsResponseDataHeader(
    var resultCode: String?,
    var resultMessage: String?
)

data class ListCardsResponseDataBody(
    var cards: MutableList<Card>?,
    var nextKey: String?
)

fun ListCardsResponse.toListCardsResponseProto(): CollectcardProto.ListCardsResponse {
    return this.dataBody?.cards?.map {
        CollectcardProto.Card
            .newBuilder()
            .setName(it.cardName)
            .setNumber(it.cardNumber)
            .setCardholderName(StringValue.of(it.cardOwnerName))
            .setIssuedDateMs(
                it.issuedAt?.let { Int64Value.of(it.toEpochSecond()) } ?: Int64Value.getDefaultInstance()
            )
            .setExpirationDateMs(
                it.expiresAt?.let { Int64Value.of(it.toEpochSecond()) } ?: Int64Value.getDefaultInstance()
            )
            .setType(StringValue.of(it.cardType))
            .setIsDormant(BoolValue.of(it.cardStatus == CardStatus.DORMANT))
//            .setIsHybrid(BoolValue.of())
//            .setIsTransitSupported(BoolValue.of())
            .build()
    }.let {
        CollectcardProto.ListCardsResponse
            .newBuilder()
            .addAllCards(it)
            .build()
    }
}
