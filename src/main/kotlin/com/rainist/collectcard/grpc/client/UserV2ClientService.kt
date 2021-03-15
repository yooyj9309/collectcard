package com.rainist.collectcard.grpc.client

import com.github.banksalad.idl.apis.v2.user.UserGrpc
import com.github.banksalad.idl.apis.v2.user.UserProto
import com.rainist.common.log.Log
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("production", "development", "staging", "local")
class UserV2ClientService(
    @Qualifier("userV2BlockingStub")
    val userV2BlockingStub: UserGrpc.UserBlockingStub
) {

    companion object : Log

    fun getUserByCi(ci: String): UserProto.UserMessage? {
        val request = UserProto.FindOneUserByCiRequest.newBuilder()
            .setCi(ci)
            .build()

        kotlin.runCatching {
            val resp = userV2BlockingStub.findOneUserByCi(request)
            return resp.user
        }
        return null
    }
}
