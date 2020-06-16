package com.rainist.collectcard.config

import com.rainist.common.service.ObjectMapperService
import com.rainist.common.service.RestTemplateService
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateConfig(
//        @Value("\${forward-proxy.host}")
//        private var forwardProxyHost: String,
//        @Value("\${forward-proxy.port}")
//        private var forwardProxyPort: Int
) {
    private val connectTimoutMs = TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS)
    private val readTimeoutMs = TimeUnit.MILLISECONDS.convert(60, TimeUnit.SECONDS)
    private val maxConnTotal = 3000
    private val maxConnPerRoute = 2000

    @Bean("commonRestTemplate")
    fun commonRestTemplate(): RestTemplate {
        val factory = HttpComponentsClientHttpRequestFactory()
        factory.setConnectTimeout(connectTimoutMs.toInt())
        factory.setReadTimeout(readTimeoutMs.toInt())
        factory.httpClient = HttpClientBuilder.create()
            .setMaxConnTotal(maxConnTotal)
            .setMaxConnPerRoute(maxConnPerRoute)
//                .setProxy(HttpHost(forwardProxyHost, forwardProxyPort))
            .build()
        return createRestTemplate(factory)
    }

    @Bean("restTemplateService")
    fun restTemplateService(
        @Qualifier("commonRestTemplate") restTemplate: RestTemplate,
        objectMapperService: ObjectMapperService
    ): RestTemplateService {
        return RestTemplateService(restTemplate, objectMapperService)
    }

    private fun createRestTemplate(factory: HttpComponentsClientHttpRequestFactory): RestTemplate {
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
