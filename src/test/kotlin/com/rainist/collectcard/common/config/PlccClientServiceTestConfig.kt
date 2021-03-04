package com.rainist.collectcard.common.config

import com.rainist.collectcard.grpc.client.PlccClientService
import org.mockito.Mockito
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

@Configuration
@Profile("test")
class PlccClientServiceTestConfig {

    @Bean
    @Primary
    fun plccClientService(): PlccClientService? {
        return Mockito.mock(PlccClientService::class.java)
    }
}
