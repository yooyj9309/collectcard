package com.rainist.collectcard.cardtransactions.mapper

import com.rainist.collectcard.cardtransactions.dto.CardTransaction
import com.rainist.collectcard.common.db.entity.CardTransactionEntity
import com.rainist.collectcard.config.MapStructConfig
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget

@Mapper(uses = [MapStructConfig.BigDecimalConverter::class])
abstract class CardTransactionMapper {

    abstract fun toTransactionDto(cardTransactionEntity: CardTransactionEntity): CardTransaction

    @AfterMapping
    fun convertTransactionDefaultValue(@MappingTarget transaction: CardTransaction) {
        transaction.installment = transaction.installment ?: 0
    }
}
