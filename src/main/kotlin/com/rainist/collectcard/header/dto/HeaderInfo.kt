package com.rainist.collectcard.header.dto

data class HeaderInfo(
    var contentType: String = "application/json",
    var clientId: String? = null,
    var banksaladUserId: String? = null,
    var organizationObjectid: String? = null
)
