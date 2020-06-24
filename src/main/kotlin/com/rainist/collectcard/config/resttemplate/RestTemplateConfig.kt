package com.rainist.collectcard.config.resttemplate

import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate

@Profile(value = ["production"])
@Configuration
class RestTemplateConfig {
    private val connectTimoutMs = TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS)
    private val readTimeoutMs = TimeUnit.MILLISECONDS.convert(60, TimeUnit.SECONDS)
    private val maxConnTotal = 3000
    private val maxConnPerRoute = 2000

    @Bean
    fun commonRestTemplate(): RestTemplate {
        val factory = HttpComponentsClientHttpRequestFactory()
        factory.setConnectTimeout(connectTimoutMs.toInt())
        factory.setReadTimeout(readTimeoutMs.toInt())
        factory.httpClient = HttpClientBuilder.create()
            .setMaxConnTotal(maxConnTotal)
            .setMaxConnPerRoute(maxConnPerRoute)
            // .setProxy(HttpHost(forwardProxyHost, forwardProxyPort))
            .build()
        return createRestTemplate(factory)
    }

    fun createRestTemplate(factory: HttpComponentsClientHttpRequestFactory): RestTemplate {
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
