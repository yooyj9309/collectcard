package com.rainist.collectcard.cardtransactions.util

import com.rainist.collectcard.cardtransactions.dto.CardTransaction
import com.rainist.collectcard.cardtransactions.entity.CardTransactionEntity
import com.rainist.collectcard.header.dto.HeaderInfo
import com.rainist.common.util.DateTimeUtil

class CardTransactionUtil {

    companion object {
        fun makeCardTransactionEntity(headerInfo: HeaderInfo, cardTransaction: CardTransaction): CardTransactionEntity {
            // TODO
            return CardTransactionEntity().apply {
                this.banksaladUserId = headerInfo.banksaladUserId?.toLong()
                this.cardCompanyId = headerInfo.organizationObjectid
                this.cardCompanyCardId = cardTransaction.cardCompanyCardId ?: ""
                this.approvalNumber = cardTransaction.approvalNumber
                this.issuedDate = DateTimeUtil.stringToLocalDateTime(cardTransaction.approvalDay!!, "yyyyMMdd", cardTransaction.approvalTime!!, "HHmmss")
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
    /*
    enum class TransactionType(val msg : String,val value : Int){
        APPROVAL("승인", 0),
        PURCHASE("매입" ,1),
        APPROVAL_CANCEL("승인 취소",2),
        PURCHASE_CANCEL("매입 취소" ,3),
        PURCHASE_PART_CANCEL("매입 부분취소",4),
        CARD_TRANSACTION_TYPE_UNKNOWN("알수없음",99);


        companion object {
            var enumMap = mutableMapOf(
                APPROVAL.value to APPROVAL,
                PURCHASE.value to PURCHASE,
                APPROVAL_CANCEL.value to APPROVAL_CANCEL,
                PURCHASE_CANCEL.value to PURCHASE_CANCEL,
                PURCHASE_PART_CANCEL.value to PURCHASE_PART_CANCEL,
                CARD_TRANSACTION_TYPE_UNKNOWN.value to CARD_TRANSACTION_TYPE_UNKNOWN
            )

        }

    }
     */
}
