package com.rainist.collectcard.common.dto

data class SyncRequest(
    var banksaladUserId: String,
    var organizationId: String
) {
    override fun toString(): String {
        return "SyncRequest(banksaladUserId='$banksaladUserId', organizationId='$organizationId')"
    }
}
