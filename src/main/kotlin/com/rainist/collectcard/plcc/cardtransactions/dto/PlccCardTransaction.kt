package com.rainist.collectcard.plcc.cardtransactions.dto

import com.rainist.common.annotation.validation.StringDateFormat
import com.rainist.common.annotation.validation.StringTimeFormat
import java.math.BigDecimal
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class PlccCardTransaction(

    @NotEmpty
    var serviceCode: String? = null,

    @NotEmpty
    var serviceName: String? = null,

    @NotEmpty
    var serviceType: String? = null,

    @StringDateFormat("yyyyMMdd")
    var approvalDay: String? = null,

    @StringTimeFormat("HHmmss")
    var approvalTime: String? = null,

    @NotEmpty
    var approvalNumber: String? = null,

    @NotEmpty
    var amount: BigDecimal? = null,

    @NotEmpty
    var discountAmount: BigDecimal? = null,

    @NotNull
    var discountRate: BigDecimal? = null,

    @NotNull
    var isInstallmentPayment: Boolean? = null,

    @NotNull
    var installment: Int? = null,

    @NotNull
    var storeNumber: String? = null,

    @NotNull
    var storeName: String? = null,

    @NotNull
    var approvalCancelType: String? = null
)
