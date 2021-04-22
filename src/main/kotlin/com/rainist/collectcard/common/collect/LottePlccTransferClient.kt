package com.rainist.collectcard.common.collect

import com.rainist.collect.common.api.Api
import com.rainist.collect.common.api.ApiResponseEntity
import com.rainist.collect.executor.ITransferClient
import com.rainist.collectcard.common.service.TransferClientLogService
import com.rainist.common.log.Log
import org.apache.http.conn.ConnectTimeoutException
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.conn.ssl.TrustStrategy
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContexts
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.StopWatch
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.SocketTimeoutException
import java.nio.charset.StandardCharsets
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext

@Component
class LottePlccTransferClient(
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

            val restTemplate = createRestTemplate()
            val res = restTemplate.exchange(req, String::class.java)

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

                logger.withFieldError("Lotte TransferClientError", it.localizedMessage, it)
            }
            .getOrThrow()
    }

    /**
     * 롯데카드 PLCC는 keep alive 를 지원하지 않음으로 매번 생성
     */
    private fun createRestTemplate(): RestTemplate {
        val connectTimoutMs = TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS)
        val readTimeoutMs = TimeUnit.MILLISECONDS.convert(60, TimeUnit.SECONDS)

        val acceptingTrustStrategy = TrustStrategy { _: Array<X509Certificate?>?, _: String? -> true }

        val sslContext: SSLContext = SSLContexts.custom()
            .loadTrustMaterial(null, acceptingTrustStrategy)
            .build()
        val csf = SSLConnectionSocketFactory(sslContext)

        val httpClient = HttpClients.custom()
            .setConnectionTimeToLive(30, TimeUnit.SECONDS)
            .setSSLSocketFactory(csf)
            .build()

        val factory = HttpComponentsClientHttpRequestFactory(httpClient)
        factory.setConnectTimeout(connectTimoutMs.toInt())
        factory.setReadTimeout(readTimeoutMs.toInt())

        return RestTemplateBuilder()
            .additionalMessageConverters(
                StringHttpMessageConverter(StandardCharsets.UTF_8),
                MappingJackson2HttpMessageConverter(),
                FormHttpMessageConverter()
            )
            .requestFactory {
                factory
            }
            .build()
    }
}
