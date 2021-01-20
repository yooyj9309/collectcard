package com.rainist.collectcard.cardbills.mapper

import com.rainist.collectcard.cardbills.dto.CardBillTransaction
import com.rainist.collectcard.common.db.entity.CardBillTransactionEntity
import com.rainist.collectcard.config.MapStructConfig
import java.math.RoundingMode
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget

@Mapper(uses = [MapStructConfig.BigDecimalConverter::class])
abstract class CardBillTransactionMapper {
    abstract fun toBillTransactionDto(cardBillTransactionEntity: CardBillTransactionEntity): CardBillTransaction

    @AfterMapping
    fun convertBillTransactionDefaultValue(@MappingTarget cardBillTransaction: CardBillTransaction) {
        cardBillTransaction.paidAmount = cardBillTransaction.paidAmount?.setScale(0, RoundingMode.DOWN)
        cardBillTransaction.billedAmount = cardBillTransaction.billedAmount?.setScale(0, RoundingMode.DOWN)
        cardBillTransaction.billedFee = cardBillTransaction.billedFee?.setScale(0, RoundingMode.DOWN)
        cardBillTransaction.remainingAmount = cardBillTransaction.remainingAmount?.setScale(0, RoundingMode.DOWN)
        cardBillTransaction.cashback = cardBillTransaction.cashback?.setScale(0, RoundingMode.DOWN)
        cardBillTransaction.pointsRate = cardBillTransaction.pointsRate?.setScale(0, RoundingMode.DOWN)
        cardBillTransaction.pointsToEarn = cardBillTransaction.pointsToEarn?.setScale(0, RoundingMode.DOWN)
        cardBillTransaction.discountAmount = cardBillTransaction.discountAmount?.setScale(0, RoundingMode.DOWN)
        cardBillTransaction.amount = cardBillTransaction.amount?.setScale(0, RoundingMode.DOWN)
        cardBillTransaction.canceledAmount = cardBillTransaction.canceledAmount?.setScale(0, RoundingMode.DOWN)
        cardBillTransaction.netSalesAmount = cardBillTransaction.netSalesAmount?.setScale(0, RoundingMode.DOWN)
        cardBillTransaction.serviceChargeAmount = cardBillTransaction.serviceChargeAmount?.setScale(0, RoundingMode.DOWN)
        cardBillTransaction.tax = cardBillTransaction.tax?.setScale(0, RoundingMode.DOWN)
        cardBillTransaction.paidPoints = cardBillTransaction.paidPoints?.setScale(0, RoundingMode.DOWN)

        if (cardBillTransaction.approvalNumber.isNullOrBlank()) {
            cardBillTransaction.approvalNumber = null
        }

        if (cardBillTransaction.approvalTime.isNullOrBlank()) {
            cardBillTransaction.approvalTime = null
        }
    }
}
