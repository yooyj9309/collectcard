package com.rainist.collectcard.common.collect.execution

import com.rainist.collect.common.dto.Execution
import com.rainist.collectcard.card.dto.ListCardsResponse
import com.rainist.collectcard.common.collect.api.ShinhancardApis

class ShinhancardExecutions {

    companion object {
        val cardShinhancardCards =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_cards)
                .to(ListCardsResponse::class.java)
                .build()
    }
}
