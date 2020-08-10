package com.rainist.collectcard.common.service

data class CardOrganization(
    var code: String? = null,
    var name: String? = null,

    var clientId: String? = null,

    var organizationId: String? = null,
    var organizationObjectId: String? = null,

    var maxMonth: Int = DEFAULT_MAX_MONTH,
    var division: Int = DEFAULT_DIVISION,
    var researchInterval: Int = DEFAULT_RESEARCH_INTERVAL
) {
    companion object {
        var DEFAULT_MAX_MONTH = 12
        var DEFAULT_DIVISION = 3
        var DEFAULT_RESEARCH_INTERVAL = 3
    }
}
