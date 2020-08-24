package com.rainist.collectcard.common.dto

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.google.protobuf.Int64Value

data class UserSyncStatusResponse(
    var dataBody: List<SyncStatusResponse>? = null
)

data class SyncStatusResponse(
    var userId: Long? = null,
    var companyId: String? = null,
    var companyType: String = "card_company",
    var syncedAt: Long? = null
)

fun UserSyncStatusResponse.toSyncStatusResponseProto(): CollectcardProto.GetSyncStatusResponse {
    dataBody?.map {
        CollectcardProto.LegacyUserAPISyncStatus
            .newBuilder()
            .setUserId(it.userId?.toInt()!!)
            .setCompanyId(it.companyId)
            .setCompanyType(it.companyType)
            .setSyncedAt(Int64Value.newBuilder().setValue(it.syncedAt!!).build())
            .build()
    }?.let {
        return CollectcardProto.GetSyncStatusResponse
            .newBuilder()
            .addAllData(it)
            .build()
    } ?: return CollectcardProto.GetSyncStatusResponse
        .newBuilder()
        .build()
}
