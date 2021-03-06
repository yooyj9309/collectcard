package com.rainist.collectcard.card

import com.rainist.collectcard.card.dto.ListCardsResponse
import com.rainist.collectcard.common.dto.CollectExecutionContext
import java.time.LocalDateTime

interface CardService {
    fun listCards(executionContext: CollectExecutionContext, now: LocalDateTime): ListCardsResponse
}
