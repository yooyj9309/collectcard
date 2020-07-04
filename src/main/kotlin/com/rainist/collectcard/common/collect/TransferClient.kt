package com.rainist.collectcard.common.collect

import com.rainist.collect.common.api.Api
import com.rainist.collect.common.api.ApiResponseEntity
import com.rainist.collect.executor.ITransferClient
import com.rainist.common.log.Log
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class TransferClient(
    val commonRestTemplate: RestTemplate
) : ITransferClient {
    companion object : Log

    override fun execute(
        url: String,
        httpMethod: Api.HttpMethod,
        headers: MutableMap<String, String>,
        body: String
    ): ApiResponseEntity {

        return kotlin.runCatching {
            val header = LinkedMultiValueMap<String, String>()
            header.setAll(headers)

            val req = RequestEntity
                .method(HttpMethod.valueOf(httpMethod.name), UriComponentsBuilder.fromHttpUrl(url).build().toUri())
                .contentType(MediaType.APPLICATION_JSON)
                .headers(HttpHeaders(header))
                .body(body)

            val res = commonRestTemplate.exchange(req, String::class.java)

            ApiResponseEntity.builder()
                .httpStatusCode(res.statusCodeValue)
                .headers(res.headers.toSingleValueMap() as Map<String, String>?)
                .body(res.body)
                .build()
        }
            .onFailure {
                logger.withFieldError("TransferClientError", it.localizedMessage, it)
            }
            .getOrThrow()
    }
}
