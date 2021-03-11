package com.rainist.collectcard.common.config

import com.rainist.collectcard.grpc.client.UserV2ClientService
import org.mockito.Mockito
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

@Configuration
@Profile("test")
class UserV2ClientServiceTestConfig {

    @Bean
    @Primary
    fun userV2ClientService(): UserV2ClientService? {
        return Mockito.mock(UserV2ClientService::class.java)
    }
}
