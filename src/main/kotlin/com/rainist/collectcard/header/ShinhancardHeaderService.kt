package com.rainist.collectcard.header

import com.rainist.collectcard.grpc.client.ConnectClient
import com.rainist.common.log.Log
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ShinhancardHeaderService(
    val connectClient: ConnectClient
) {
    companion object : Log

    @Value("\${shinhancard.clientId}")
    lateinit var clientId: String

    fun getHeader(banksaladUserId: String, organizationObjectId: String): MutableMap<String, String> {
        return kotlin.runCatching {
            mutableMapOf(
                "contentType" to "application/json",
                "authorization" to "Bearer ${connectClient.getAccessToken(banksaladUserId, organizationObjectId)}",
                "clientId" to clientId
            )
        }
        .onSuccess {
            it.iterator().forEach {
                logger.info("shinhan header : {} , {}", it.key, it.value)
            }
        }
        .onFailure {
            logger.withFieldError("ShinhancardHeaderServiceGetHeader", it.localizedMessage, it)
        }
        .getOrThrow()
    }
}
