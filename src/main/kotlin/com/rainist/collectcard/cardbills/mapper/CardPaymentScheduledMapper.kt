package com.rainist.collectcard.cardbills.mapper

import com.rainist.collectcard.cardbills.dto.CardBillTransaction
import com.rainist.collectcard.common.db.entity.CardPaymentScheduledEntity
import com.rainist.collectcard.common.util.CustomStringUtil
import com.rainist.collectcard.config.MapStructConfig
import java.math.BigDecimal
import java.math.RoundingMode
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings

@Mapper(uses = [MapStructConfig.BigDecimalConverter::class])
abstract class CardPaymentScheduledMapper {

    @Mappings(
        value = [
            Mapping(source = "cardNumberMask", target = "cardNumberMasked")
        ]
    )
    abstract fun toBillTransactionDto(cardPaymentScheduledEntity: CardPaymentScheduledEntity): CardBillTransaction

    @AfterMapping
    fun convertBillTransactionDefaultValue(@MappingTarget cardBillTransaction: CardBillTransaction) {
        // dto로 변환할 때 cardNumber의 5,6번째 번호를 *로 마스킹하는 로직
        cardBillTransaction.cardNumber = CustomStringUtil.replaceNumberToMask(cardBillTransaction.cardNumber)
        // dto로 변환할 때 cardNumberMasked의 5,6번째 번호를 *로 마스킹하는 로직
        cardBillTransaction.cardNumberMasked =
            CustomStringUtil.replaceMaskedNumberToMask(cardBillTransaction.cardNumberMasked)
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
        cardBillTransaction.serviceChargeAmount =
            cardBillTransaction.serviceChargeAmount?.setScale(0, RoundingMode.DOWN)
        cardBillTransaction.tax = cardBillTransaction.tax?.setScale(0, RoundingMode.DOWN)
        cardBillTransaction.paidPoints = cardBillTransaction.paidPoints?.setScale(0, RoundingMode.DOWN)

        if (cardBillTransaction.approvalNumber.isNullOrBlank()) {
            cardBillTransaction.approvalNumber = null
        }

        if (cardBillTransaction.approvalTime.isNullOrBlank()) {
            cardBillTransaction.approvalTime = null
        }

        // db에서 ""로 저장되니 dto로 변환할 때 null로 변경
        if (cardBillTransaction.cardCompanyCardId.isNullOrBlank()) {
            cardBillTransaction.cardCompanyCardId = null
        }

        // installment == 0이면 null로 변경
        if (cardBillTransaction.installment == 0) {
            cardBillTransaction.installment = null
        }

        // netSalesAmount == 0 이면 null로 변경
        if (cardBillTransaction.netSalesAmount == BigDecimal("0.0000")) {
            cardBillTransaction.netSalesAmount = null
        }

        // paymentDay == "" 경우 null로 변경
        if (cardBillTransaction.paymentDay.isNullOrBlank()) {
            cardBillTransaction.paymentDay = null
        }
    }
}
