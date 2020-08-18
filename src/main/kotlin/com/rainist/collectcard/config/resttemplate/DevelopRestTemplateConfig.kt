package com.rainist.collectcard.config.resttemplate

import java.nio.charset.StandardCharsets
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.conn.ssl.TrustStrategy
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContexts
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate

@Profile(value = ["local", "development", "test"])
@Configuration
class DevelopRestTemplateConfig {
    private val connectTimoutMs = TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS)
    private val readTimeoutMs = TimeUnit.MILLISECONDS.convert(60, TimeUnit.SECONDS)
    private val maxConnTotal = 3000
    private val maxConnPerRoute = 2000

    @Bean
    fun commonRestTemplate(): RestTemplate {
        val acceptingTrustStrategy = TrustStrategy { _: Array<X509Certificate?>?, _: String? -> true }

        val sslContext: SSLContext = SSLContexts.custom()
            .loadTrustMaterial(null, acceptingTrustStrategy)
            .build()
        val csf = SSLConnectionSocketFactory(sslContext)

        val httpClient = HttpClients.custom()
            .setMaxConnTotal(maxConnTotal)
            .setMaxConnPerRoute(maxConnPerRoute)
            .setConnectionTimeToLive(30, TimeUnit.SECONDS)
            .setSSLSocketFactory(csf)
            .build()

        val factory = HttpComponentsClientHttpRequestFactory(httpClient)
        factory.setConnectTimeout(connectTimoutMs.toInt())
        factory.setReadTimeout(readTimeoutMs.toInt())

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
