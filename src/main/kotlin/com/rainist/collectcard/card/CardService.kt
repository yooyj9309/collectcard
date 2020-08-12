package com.rainist.collectcard.card

import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collectcard.card.dto.ListCardsResponse

interface CardService {
    fun listCards(executionContext: ExecutionContext): ListCardsResponse
}
