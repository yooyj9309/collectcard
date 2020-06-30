package com.rainist.collectcard.header

import com.rainist.collectcard.grpc.client.ConnectClient
import com.rainist.collectcard.header.dto.HeaderInfo
import com.rainist.common.exception.UnknownException
import com.rainist.common.log.Log
import org.springframework.stereotype.Service

@Service
class HeaderService(
    val connectClient: ConnectClient
) {
    companion object : Log

    fun getHeader(headerInfo: HeaderInfo): MutableMap<String, String?> {
        return kotlin.runCatching {
            let {
                // TODO 예상국 해당 로직은 신한카드는 현재 unique token 로직이 아니지만 타사의 경우 갱신시 다른 API 조회에서 막힐수 있음
                // TODO 그러므로, collect 는 getToken 만 하며, refresh 여부는 connect 에서 시간으로 판단하여 진행하는 로직으로 변경 필요
                connectClient.refreshToken(headerInfo.banksaladUserId, headerInfo.organizationObjectId)?.accessToken
            }
            ?.let { accessToken ->
                mutableMapOf(
                    "contentType" to headerInfo.contentType,
                    "authorization" to "Bearer $accessToken",
                    "clientId" to headerInfo.clientId
                )
            }
            ?: kotlin.run {
                throw UnknownException("get header token issue error")
            }
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
