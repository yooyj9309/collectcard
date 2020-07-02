package com.rainist.collectcard.common.organization

import javax.validation.constraints.NotEmpty

class CardOrganization(
    var code: String? = null,
    var name: String? = null,

    @field:NotEmpty
    var clientId: String? = null,

    @field:NotEmpty
    var organizationObjectId: String? = null,

    var maxMonth: Int = 12,
    var division: Long = 4
)
