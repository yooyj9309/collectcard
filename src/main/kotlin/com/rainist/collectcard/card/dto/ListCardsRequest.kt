package com.rainist.collectcard.card.dto

data class ListCardsRequest(
    var dataBody: ListCardsRequestDataBody?
)

data class ListCardsRequestDataBody(
    var nextQueryKey: String?
)
