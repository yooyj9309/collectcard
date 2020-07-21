package com.rainist.collectcard.cardtransactions.util

import com.rainist.collectcard.cardtransactions.dto.CardTransaction
import com.rainist.collectcard.common.db.entity.CardTransactionEntity
import com.rainist.common.util.DateTimeUtil

class CardTransactionUtil {

    companion object {
        fun makeCardTransactionEntity(banksaladUserId: Long, organizationObjectId: String, cardTransaction: CardTransaction): CardTransactionEntity {
            // TODO
            return CardTransactionEntity().apply {
                this.banksaladUserId = banksaladUserId
                this.cardCompanyId = organizationObjectId
                this.cardCompanyCardId = cardTransaction.cardCompanyCardId ?: ""
                this.approvalNumber = cardTransaction.approvalNumber
                this.issuedDate = DateTimeUtil.stringToLocalDateTime(
                    cardTransaction.approvalDay!!,
                    "yyyyMMdd",
                    cardTransaction.approvalTime!!,
                    "HHmmss"
                )
                this.cardName = cardTransaction.cardName
                this.cardNumber = cardTransaction.cardNumber
                this.cardNumberMask = cardTransaction.cardNumberMask
                this.businessLicenseNumber = cardTransaction.businessLicenseNumber
                this.storeName = cardTransaction.storeName
                this.storeNumber = cardTransaction.storeNumber
                this.cardType = cardTransaction.cardType
                this.cardTransactionType = cardTransaction.cardTransactionType?.name
                this.currency = cardTransaction.currencyCode ?: "KRW"
                this.isInstallmentPayment = cardTransaction.isInstallmentPayment ?: false
            }
        }
    }
}
