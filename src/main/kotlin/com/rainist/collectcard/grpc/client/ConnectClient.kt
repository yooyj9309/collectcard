package com.rainist.collectcard.grpc.client

import com.github.banksalad.idl.apis.external.v1.connect.ConnectGrpc
import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto
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

    fun refreshToken(banksaladUserId: String?, organizationObjectid: String?): ConnectProto.RefreshTokenResponse? {
        return kotlin.runCatching {
            ConnectProto.RefreshTokenRequest
                .newBuilder()
                .setBanksaladUserId(banksaladUserId)
                .setOrganizationObjectid(organizationObjectid)
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

    fun issueToken(authorizationCode: String, banksaladUserId: String, organizationObjectid: String): ConnectProto.IssueTokenResponse? {
        return kotlin.runCatching {
            ConnectProto.IssueTokenRequest
                .newBuilder()
                .setAuthorizationCode(authorizationCode)
                .setBanksaladUserId(banksaladUserId)
                .setOrganizationObjectid(organizationObjectid)
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

    fun getAccessToken(banksaladUserId: String?, organizationObjectid: String?): ConnectProto.GetAccessTokenResponse? {
        return kotlin.runCatching {
            ConnectProto.GetAccessTokenRequest
                .newBuilder()
                .setBanksaladUserId(banksaladUserId)
                .setOrganizationObjectid(organizationObjectid)
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
