package com.rainist.collectcard.card

import com.rainist.collectcard.card.dto.ListCardsRequest
import com.rainist.collectcard.card.dto.ListCardsResponse

interface CardService {
    fun listCards(header: MutableMap<String, String?>, listCardsRequest: ListCardsRequest): ListCardsResponse
}
