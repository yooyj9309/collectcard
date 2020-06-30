package com.rainist.collectcard.header

import com.rainist.collectcard.grpc.client.ConnectClient
import com.rainist.collectcard.header.dto.HeaderInfo
import com.rainist.common.log.Log
import org.springframework.stereotype.Service

@Service
class HeaderService(
    val connectClient: ConnectClient
) {
    companion object : Log

    fun getHeader(headerInfo: HeaderInfo): MutableMap<String, String?> {
        return kotlin.runCatching {
            mutableMapOf(
                "contentType" to headerInfo.contentType,
                "authorization" to "Bearer ${connectClient.getAccessToken(headerInfo.banksaladUserId, headerInfo.organizationObjectId)?.accessToken}",
                "clientId" to headerInfo.clientId
            )
        }
        .onSuccess {
            it.iterator().forEach {
                logger.info("header : {} , {}", it.key, it.value)
            }
        }
        .onFailure {
            logger.withFieldError("HeaderService GetHeader", it.localizedMessage, it)
        }
        .getOrThrow()
    }
}
