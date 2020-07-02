package com.rainist.collectcard.common.organization

import com.rainist.collectcard.cardtransactions.CardTransactionServiceImpl.Companion.DEFAULT_DIVISION
import com.rainist.collectcard.cardtransactions.CardTransactionServiceImpl.Companion.DEFAULT_MAX_MONTH
import javax.validation.constraints.NotEmpty

data class CardOrganization(
    var code: String? = null,
    var name: String? = null,

    @field:NotEmpty
    var clientId: String? = null,

    @field:NotEmpty
    var organizationObjectId: String? = null,

    var maxMonth: Long = DEFAULT_MAX_MONTH,
    var division: Int = DEFAULT_DIVISION
)
