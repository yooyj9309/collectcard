package com.rainist.collectcard.common.dto

data class SyncRequest(
    var banksaladUserId: Long,
    var organizationId: String
) {
    override fun toString(): String {
        return "SyncRequest(banksaladUserId='$banksaladUserId', organizationId='$organizationId')"
    }
}
