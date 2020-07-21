package com.rainist.collectcard.card.dto

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.google.protobuf.BoolValue
import com.google.protobuf.StringValue
import java.time.format.DateTimeFormatter

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
            .setUserName(StringValue.of(it.cardOwnerName))
            .setIssuedDate(
                it.issuedDate?.format(DateTimeFormatter.ISO_LOCAL_DATE)?.let { StringValue.of(it) }
                    ?: StringValue.getDefaultInstance()
            )
            .setExpirationDate(
                it.expiresDate?.format(DateTimeFormatter.ISO_LOCAL_DATE)?.let { StringValue.of(it) }
                    ?: StringValue.getDefaultInstance()
            )
            .setType(StringValue.of(it.cardType))
            .setDormant(BoolValue.of(it.cardStatus == CardStatus.DORMANT))
            .setHybrid(BoolValue.getDefaultInstance())
            .setTrafficSupported(BoolValue.getDefaultInstance())
            .build()
    }.let {
        CollectcardProto.ListCardsResponse
            .newBuilder()
            .addAllData(it)
            .build()
    }
}
