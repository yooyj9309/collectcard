package com.rainist.collectcard.common.collect

import com.rainist.collect.common.api.Api
import com.rainist.collect.common.api.ApiResponseEntity
import com.rainist.collect.executor.ITransferClient
import com.rainist.collectcard.common.service.TransferClientLogService
import com.rainist.common.log.Log
import java.net.SocketTimeoutException
import org.apache.http.conn.ConnectTimeoutException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.StopWatch
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class TransferClient(
    val commonRestTemplate: RestTemplate,
    val transferClientLogService: TransferClientLogService
) : ITransferClient {
    companion object : Log

    override fun execute(
        url: String,
        httpMethod: Api.HttpMethod,
        headers: MutableMap<String, String>,
        body: String
    ): ApiResponseEntity {

        val stopWatch = StopWatch()
        val uri = UriComponentsBuilder.fromHttpUrl(url).build().toUri()

        return kotlin.runCatching {
            val header = LinkedMultiValueMap<String, String>()
            header.setAll(headers)

            val req = RequestEntity
                .method(HttpMethod.valueOf(httpMethod.name), uri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(HttpHeaders(header))
                .body(body)

            stopWatch.start()
            val res = commonRestTemplate.exchange(req, String::class.java)
            stopWatch.stop()

            transferClientLogService.loggingSuccessCount(uri, HttpStatus.OK)
            transferClientLogService.loggingTiming(uri, stopWatch.totalTimeMillis)

            ApiResponseEntity.builder()
                .httpStatusCode(res.statusCodeValue)
                .headers(res.headers.toSingleValueMap() as Map<String, String>?)
                .body(res.body)
                .build()
        }
        .onFailure {
            when (it) {
                is HttpStatusCodeException -> {
                    transferClientLogService.loggingFailureCount(uri, it.statusCode)
                }
                is ResourceAccessException, is SocketTimeoutException, is ConnectTimeoutException -> {
                    transferClientLogService.loggingTimeoutCount(uri)
                }
                else -> {
                    transferClientLogService.loggingUnknownErrorCount(uri)
                }
            }

            logger.withFieldError("TransferClientError", it.localizedMessage, it)
        }
        .getOrThrow()
    }
}
