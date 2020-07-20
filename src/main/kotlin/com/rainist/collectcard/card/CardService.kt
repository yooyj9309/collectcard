package com.rainist.collectcard.card

import com.rainist.collectcard.card.dto.ListCardsResponse
import com.rainist.collectcard.common.service.CardOrganization

interface CardService {
    fun listCards(banksaladUserId: String, organization: CardOrganization): ListCardsResponse
}
