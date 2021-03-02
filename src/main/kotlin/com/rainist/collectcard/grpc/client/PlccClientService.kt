package com.rainist.collectcard.grpc.client

import com.github.banksalad.idl.apis.v1.plcc.PlccGrpc
import com.github.banksalad.idl.apis.v1.plcc.PlccProto
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("production", "development", "staging", "local")
class PlccClientService(
    @Qualifier("plccBlockingStub")
    val plccBlockingStub: PlccGrpc.PlccBlockingStub
) {

    fun syncPlccsByCollectcardData(
        organizationId: String,
        ci: String,
        data: List<String>, // todo: fix type
        syncType: String // todo: fix type
    ): PlccProto.SyncPlccsByCollectcardDataResponse? {
        val request = PlccProto.SyncPlccsByCollectcardDataRequest.newBuilder()
            .setOrganizationId(organizationId)
            .setCi(ci)
//            .addAllData(data)
//            .setSyncType(syncType)
            .build()
        return plccBlockingStub.syncPlccsByCollectcardData(request)
    }
}
