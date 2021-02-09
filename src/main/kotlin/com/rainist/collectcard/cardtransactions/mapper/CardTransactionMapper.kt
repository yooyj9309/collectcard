package com.rainist.collectcard.cardtransactions.mapper

import com.rainist.collectcard.cardtransactions.dto.CardTransaction
import com.rainist.collectcard.common.db.entity.CardTransactionEntity
import com.rainist.collectcard.common.util.CustomStringUtil
import com.rainist.collectcard.config.MapStructConfig
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget

@Mapper(uses = [MapStructConfig.BigDecimalConverter::class])
abstract class CardTransactionMapper {

    abstract fun toTransactionDto(cardTransactionEntity: CardTransactionEntity): CardTransaction

    @AfterMapping
    fun convertTransactionDefaultValue(@MappingTarget transaction: CardTransaction) {
        transaction.cardNumber = CustomStringUtil.replaceNumberToMask(transaction.cardNumber)
        transaction.installment = transaction.installment ?: 0
        // old 데이터에는 null이라서 맞추기 위함
        transaction.cardTransactionId = null
    }
}
