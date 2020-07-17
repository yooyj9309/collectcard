package com.rainist.collectcard.card

import com.rainist.collectcard.card.dto.ListCardsResponse

interface CardService {
    fun listCards(banksaladUserId: String, organizationId: String): ListCardsResponse
}
