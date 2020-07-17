package com.rainist.collectcard.common.service

data class CardOrganization(
    var code: String? = null,
    var name: String? = null,

    var clientId: String? = null,

    var organizationId: String? = null,
    var organizationObjectId: String? = null,

    var maxMonth: Int = DEFAULT_MAX_MONTH,
    var division: Int = DEFAULT_DIVISION
) {
    companion object {
        var DEFAULT_MAX_MONTH = 12
        var DEFAULT_DIVISION = 3
    }
}
