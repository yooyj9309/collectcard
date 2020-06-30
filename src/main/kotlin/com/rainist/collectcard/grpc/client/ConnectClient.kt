package com.rainist.collectcard.grpc.client

import com.github.rainist.idl.apis.external.v1.connect.ConnectGrpc
import com.github.rainist.idl.apis.external.v1.connect.ConnectProto
import com.rainist.common.log.Log
import io.grpc.ManagedChannel
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class ConnectClient(
    @Qualifier("connectChannel")
    val connectChannel: ManagedChannel
) {
    companion object : Log

    fun refreshToken(banksaladUserId: String?, organizationObjectId: String?): ConnectProto.RefreshTokenResponse? {
        return kotlin.runCatching {
            ConnectProto.RefreshTokenRequest
                .newBuilder()
                .setBanksaladUserId(banksaladUserId)
                .setOrganizationObjectid(organizationObjectId)
                .build()
                .run {
                    ConnectGrpc.newBlockingStub(connectChannel).refreshToken(this)
                }
        }
        .onFailure {
            logger.withFieldError("refreshTokenError", it.localizedMessage, it)
        }
        .getOrThrow()
    }

    fun issueToken(authorizationCode: String, banksaladUserId: String, organizationObjectId: String): ConnectProto.IssueTokenResponse? {
        return kotlin.runCatching {
            ConnectProto.IssueTokenRequest
                .newBuilder()
                .setAuthorizationCode(authorizationCode)
                .setBanksaladUserId(banksaladUserId)
                .setOrganizationObjectid(organizationObjectId)
                .build()
                .run {
                    ConnectGrpc.newBlockingStub(connectChannel).issueToken(this)
                }
        }
        .onFailure {
            logger.withFieldError("issueTokenError", it.localizedMessage, it)
        }
        .getOrThrow()
    }

    fun getAccessToken(banksaladUserId: String?, organizationObjectId: String?): ConnectProto.GetAccessTokenResponse? {
        return kotlin.runCatching {
            ConnectProto.GetAccessTokenRequest
                .newBuilder()
                .setBanksaladUserId(banksaladUserId)
                .setOrganizationObjectid(organizationObjectId)
                .build()
                .run {
                    ConnectGrpc.newBlockingStub(connectChannel).getAccessToken(this)
                }
        }
        .onFailure {
            logger.withFieldError("issueTokenError", it.localizedMessage, it)
        }
        .getOrThrow()
    }
}
