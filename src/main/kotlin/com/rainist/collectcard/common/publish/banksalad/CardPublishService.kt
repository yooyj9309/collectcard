package com.rainist.collectcard.common.publish.banksalad

import com.rainist.collectcard.card.dto.ListCardsResponse
import com.rainist.collectcard.card.mapper.CardMapper
import com.rainist.collectcard.common.db.repository.CardRepository
import com.rainist.collectcard.common.dto.CollectShadowingResponse
import com.rainist.common.log.Log
import io.micrometer.core.instrument.MeterRegistry
import java.time.LocalDateTime
import org.apache.commons.lang3.builder.EqualsBuilder
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service

@Service
class CardPublishService(
    val cardRepository: CardRepository,
    val meterRegistry: MeterRegistry
) {

    private companion object : Log

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
        val isShadowingDiff = EqualsBuilder.reflectionEquals(cards, oldCards)

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
}
