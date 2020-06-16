package com.rainist.collectcard.common.collect

import com.rainist.collect.common.dto.Api
import com.rainist.collect.common.dto.TransferResponseEntity
import com.rainist.collect.executor.service.ITransferClient
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class TransferClient(val commonRestTemplate: RestTemplate) : ITransferClient {
    override fun execute(
        url: String,
        httpMethod: Api.HttpMethod,
        headers: MutableMap<String, String>,
        body: String
    ): TransferResponseEntity {
        try {
            val responseEntity = commonRestTemplate.postForEntity(url, body, String::class.java)

            return TransferResponseEntity.builder()
                .httpStatusCode(responseEntity.statusCodeValue)
                .headers(responseEntity.headers.toSingleValueMap() as Map<String, String>?)
                .body(responseEntity.body)
                .build()
        } catch (t: Throwable) {
            t.printStackTrace()
            throw RuntimeException(t)
        }
    }
}
