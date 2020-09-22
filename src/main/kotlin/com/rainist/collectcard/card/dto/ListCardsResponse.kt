package com.rainist.collectcard.card.dto

import com.github.banksalad.idl.apis.v1.collectcard.CollectcardProto
import com.google.protobuf.BoolValue
import com.google.protobuf.StringValue
import com.rainist.collectcard.common.enums.CardStatus
import com.rainist.collectcard.common.enums.CardType
import com.rainist.collectcard.common.enums.ResultCode

data class ListCardsResponse(
    var resultCodes: MutableList<ResultCode> = mutableListOf(),
    var dataHeader: ListCardsResponseDataHeader?,
    var dataBody: ListCardsResponseDataBody?
)

data class ListCardsResponseDataHeader(
    var resultCode: ResultCode?,
    var resultMessage: String?
)

data class ListCardsResponseDataBody(
    var cards: MutableList<Card>?,
    var nextKey: String?
)

fun ListCardsResponse.toListCardsResponseProto(): CollectcardProto.ListCardsResponse {
    // TODO 박두상 : 현재 connectCard diff 작업을 위하여 IssuedDate, ExpirationDate, Hybrid 주석처리, cardType String 소문자 처리
    return this.dataBody?.cards?.map { card ->
        CollectcardProto.Card
            .newBuilder()
            .setName(card.cardName)
            .setNumber(card.cardNumber)
            .setUserName(StringValue.of(card.cardOwnerName))
            .setType(if (card.cardType == CardType.DEBIT) StringValue.of(CardType.CHECK.name.toLowerCase()) else StringValue.of(card.cardType.name.toLowerCase()))
            .setDormant(BoolValue.of(card.cardStatus == CardStatus.DORMANT))
//            .setHybrid(BoolValue.getDefaultInstance())
//            .setIssuedDate(
//                card.issuedDay?.format(DateTimeFormatter.ISO_LOCAL_DATE)?.let { StringValue.of(it) }
//                    ?: StringValue.getDefaultInstance()
//            )
//            .setExpirationDate(
//                card.expiresDay?.format(DateTimeFormatter.ISO_LOCAL_DATE)?.let { StringValue.of(it) }
//                    ?: StringValue.getDefaultInstance()
//            )
            .setTrafficSupported(BoolValue.of(card.trafficSupported))
            .build()
    }.let {
        CollectcardProto.ListCardsResponse
            .newBuilder()
            .addAllData(it ?: mutableListOf())
            .build()
    }
}
