package com.rainist.collectcard.cardtransactions.mapper

import com.rainist.collectcard.cardtransactions.dto.CardTransaction
import com.rainist.collectcard.common.db.entity.CardTransactionEntity
import com.rainist.collectcard.config.MapStructConfig
import org.mapstruct.Mapper

@Mapper(uses = [MapStructConfig.BigDecimalConverter::class])
interface CardTransactionMapper {

    fun toTransactionDto(cardTransactionEntity: CardTransactionEntity): CardTransaction
}
