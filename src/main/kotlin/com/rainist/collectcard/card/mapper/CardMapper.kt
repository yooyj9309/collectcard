package com.rainist.collectcard.card.mapper

import com.rainist.collectcard.card.dto.Card
import com.rainist.collectcard.common.db.entity.CardEntity
import com.rainist.collectcard.common.db.entity.CardHistoryEntity
import com.rainist.collectcard.config.MapStructConfig
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings

@Mapper(uses = [MapStructConfig.BigDecimalConverter::class])
interface CardMapper {

    @Mappings(
        value = [
            Mapping(target = "cardId", ignore = true),
            Mapping(source = "expirationDay", target = "expiresDay")
        ]
    )
    fun toCardDto(cardEntity: CardEntity): Card

    @Mappings(
        value = [
            Mapping(source = "expiresDay", target = "expirationDay")
        ]
    )
    fun toCardEntity(card: Card): CardEntity

    @Mappings(
        value = [
            Mapping(target = "createdAt", ignore = true),
            Mapping(target = "updatedAt", ignore = true)
        ]
    )
    fun toCardHistoryEntity(cardEntity: CardEntity): CardHistoryEntity

    @Mapping(target = "cardId", ignore = true)
    fun merge(card: Card, @MappingTarget cardEntity: CardEntity)
}
