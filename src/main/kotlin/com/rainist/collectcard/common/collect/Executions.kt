package com.rainist.collectcard.common.collect

import com.rainist.collect.common.dto.Api
import com.rainist.collect.common.dto.Execution
import com.rainist.collectcard.card.dto.ListCardsResponse

class Executions() {

    companion object {
        val cardShinhancardCards =
            Execution.create()
                .exchange(Apis.card_shinhancard_cards)
                .to(ListCardsResponse::class.java)
                .build()

        fun valueOf(businessType: BusinessType, organization: Organization, transaction: Transaction): Execution {
            return map.get(
                Api.builder()
                    .business(businessType.name)
                    .agency(organization.name)
                    .transaction(transaction.name)
                    .build()
            ) ?: throw RuntimeException()
        }

        private val map = mapOf<Api, Execution>(
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.cards.name)
                .build() to cardShinhancardCards
        )
    }
}
