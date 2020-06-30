package com.rainist.collectcard.card.dto

data class ListCardsRequest(
    var dataBody: ListCardsRequestDataBody? = null
)

data class ListCardsRequestDataBody(
    var nextKey: String? = ""
)
