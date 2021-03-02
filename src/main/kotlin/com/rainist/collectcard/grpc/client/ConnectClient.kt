package com.rainist.collectcard.grpc.client

import com.github.banksalad.idl.apis.v1.connect.ConnectGrpc
import com.github.banksalad.idl.apis.v1.connect.ConnectProto
import com.rainist.common.log.Log
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class ConnectClient(
    @Qualifier("connectStub")
    val connectStub: ConnectGrpc.ConnectBlockingStub
) {
    private companion object : Log

    fun refreshToken(banksaladUserId: String?, organizationObjectid: String?): ConnectProto.RefreshTokenResponse? {
        return kotlin.runCatching {
            ConnectProto.RefreshTokenRequest
                .newBuilder()
                .setBanksaladUserId(banksaladUserId)
                .setOrganizationObjectid(organizationObjectid)
                .build()
                .run {
                    connectStub.refreshToken(this)
                }
        }
        .onFailure {
            logger.With("refreshTokenError", it.localizedMessage).error("", it)
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
                    connectStub.issueToken(this)
                }
        }
        .onFailure {
            logger.With("issueTokenError", it.localizedMessage).error("", it)
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
                    connectStub.getAccessToken(this)
                }
        }
        .onFailure {
            logger.With("getAccessTokenError", it.localizedMessage).error("", it)
        }
        .getOrThrow()
    }
}
