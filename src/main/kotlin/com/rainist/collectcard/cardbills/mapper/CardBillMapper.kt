package com.rainist.collectcard.cardbills.mapper

import com.rainist.collectcard.cardbills.dto.CardBill
import com.rainist.collectcard.common.db.entity.CardBillEntity
import com.rainist.collectcard.config.MapStructConfig
import java.math.BigDecimal
import java.math.RoundingMode
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget

@Mapper(uses = [MapStructConfig.BigDecimalConverter::class])
abstract class CardBillMapper {
    abstract fun toCardBillDto(cardBillEntity: CardBillEntity): CardBill

    @AfterMapping
    fun convertBillDefaultValue(@MappingTarget cardBill: CardBill) {
        if (cardBill.prepaidAmount == BigDecimal("0.0000")) {
            cardBill.prepaidAmount = null
        }

        cardBill.billingAmount = cardBill.billingAmount?.setScale(0, RoundingMode.DOWN)
    }
}
