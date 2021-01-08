package com.rainist.collectcard.common.publish.banksalad

import com.rainist.collectcard.card.dto.Card
import com.rainist.collectcard.card.dto.ListCardsResponse
import com.rainist.collectcard.card.mapper.CardMapper
import com.rainist.collectcard.common.db.repository.CardRepository
import com.rainist.collectcard.common.dto.CollectShadowingResponse
import java.time.LocalDateTime
import org.apache.commons.lang3.builder.EqualsBuilder
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service

@Service
class CardPublishService(
    val cardRepository: CardRepository
) {

    // TODO [FLOW] 해당부분을 제외하고 Diff 비교, 추후 필수로 제거필요.
    val CARD_SHADOWING_EXCLUDE_EQUALS_FIELD = mutableListOf(
        Card::cardId.name
    )

    val cardMapper = Mappers.getMapper(CardMapper::class.java)

    fun sync(banksaladUserId: Long, organizationId: String, lastCheckAt: LocalDateTime) {
        TODO()
    }

    fun shadowing(banksaladUserId: Long, organizationId: String, lastCheckAt: LocalDateTime?, executionRequestId: String, oldResponse: ListCardsResponse): CollectShadowingResponse {
        val cards = cardRepository.findAllByBanksaladUserIdAndCardCompanyIdAndLastCheckAt(
            banksaladUserId, organizationId, lastCheckAt
        ).map {
            cardMapper.toCardDto(it)
        }.sortedWith(compareBy({ it.cardName }, { it.cardNumber }))

        val oldCards = oldResponse.dataBody?.cards?.sortedWith(compareBy({ it.cardName }, { it.cardNumber })) ?: mutableListOf()
        val isShadowingDiff = unequals(cards, oldCards)

        return CollectShadowingResponse(
            banksaladUserId = banksaladUserId,
            organizationId = organizationId,
            lastCheckAt = lastCheckAt.toString(),
            executionRequestId = executionRequestId,
            isDiff = isShadowingDiff,
            executionName = "card",
            oldList = oldCards,
            dbList = cards
        )
    }

    private fun unequals(cards: List<Card>, oldCards: List<Card>): Boolean {
        if (cards.size != oldCards.size)
            return true

        for (i in cards.indices) {
            if (!EqualsBuilder.reflectionEquals(cards[i], oldCards[i], CARD_SHADOWING_EXCLUDE_EQUALS_FIELD)) {
                return true
            }
        }
        return false
    }
}
