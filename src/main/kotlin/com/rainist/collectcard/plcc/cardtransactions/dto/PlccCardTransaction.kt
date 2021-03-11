package com.rainist.collectcard.plcc.cardtransactions.dto

import com.rainist.collectcard.common.enums.CardTransactionType
import com.rainist.collectcard.common.enums.CardType
import com.rainist.collectcard.plcc.cardtransactions.enums.PlccCardServiceType
import com.rainist.common.annotation.validation.StringDateFormat
import com.rainist.common.annotation.validation.StringTimeFormat
import java.math.BigDecimal
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class PlccCardTransaction(

    @NotEmpty
    var serviceCode: String? = null,

    var serviceCodeOrigin: String? = null,

    @NotEmpty
    var serviceName: String? = null,

    @NotEmpty
    var serviceType: PlccCardServiceType = PlccCardServiceType.REWARDS_SERVICE_TYPE_UNKNOWN,

    var serviceTypeOrigin: String? = null,

    @StringDateFormat("yyyyMMdd")
    var approvalDay: String? = null,

    @StringTimeFormat("HHmmss")
    var approvalTime: String? = null,

    var cardName: String? = null,

    var cardNumber: String? = null,

    var cardNumberMask: String? = null,

    @NotEmpty
    var approvalNumber: String? = null,

    @NotEmpty
    var amount: BigDecimal? = null,

    @NotEmpty
    var discountAmount: BigDecimal? = null,

    @NotNull
    var discountRate: BigDecimal? = null,

    var partialCanceledAmount: BigDecimal? = null,

    var tax: BigDecimal? = null,

    var serviceChargeAmount: BigDecimal? = null,

    var netSalesAmount: BigDecimal? = null,

    var businessLicenseNumber: String? = null,

    @NotNull
    var isInstallmentPayment: Boolean? = null,

    @NotNull
    var installment: Int? = null,

    var paymentDay: String? = null,

    var isOverseaUse: Boolean? = null,

    @NotNull
    var storeNumber: String? = null,

    var storeCategory: String? = null,

    var cardType: CardType? = null,

    var cardTypeOrigin: String? = null,

    @NotNull
    var storeName: String? = null,

    @NotNull
    var cardTransactionType: CardTransactionType = CardTransactionType.CARD_TRANSACTION_TYPE_UNKNOWN, // 정상 0, 취소 1

    var cardTransactionTypeOrigin: String? = null,

    var currencyCode: String? = null,

    var transactionCountry: String? = null
)
