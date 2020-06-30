package com.rainist.collectcard.card.dto

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
