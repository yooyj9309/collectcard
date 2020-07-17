package com.rainist.collectcard.common.service

import com.rainist.collectcard.cardtransactions.CardTransactionServiceImpl.Companion.DEFAULT_DIVISION
import com.rainist.collectcard.cardtransactions.CardTransactionServiceImpl.Companion.DEFAULT_MAX_MONTH

data class Organization(
    var code: String? = null,
    var name: String? = null,

    var clientId: String? = null,

    var organizationId: String? = null,
    var organizationObjectId: String? = null,

    var maxMonth: Long = DEFAULT_MAX_MONTH,
    var division: Int = DEFAULT_DIVISION
)
