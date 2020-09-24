package com.rainist.collectcard.common.service

import com.rainist.collectcard.grpc.client.ConnectClient
import com.rainist.common.log.Log
import org.springframework.http.MediaType
import org.springframework.stereotype.Service

@Service
class HeaderService(
    val connectClient: ConnectClient,
    val organizationService: OrganizationService
) {
    companion object : Log

    val CONTENT_TYPE = "contentType"
    val AUTHORIZATION = "authorization"
    val CALIENT_ID = "clientId"

    fun makeHeader(banksaladUserId: String, organizationId: String): MutableMap<String, String?> {
        val organization = organizationService.getOrganizationByOrganizationId(organizationId)
        val accessToken =
            connectClient.getAccessToken(banksaladUserId, organization.organizationObjectId)?.accessToken

        return mutableMapOf(
            CONTENT_TYPE to MediaType.APPLICATION_JSON_VALUE,
            AUTHORIZATION to "Bearer $accessToken",
            CALIENT_ID to organization.clientId
        )
    }
}
