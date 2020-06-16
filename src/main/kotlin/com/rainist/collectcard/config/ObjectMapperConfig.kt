package com.rainist.collectcard.config

import com.rainist.common.service.ObjectMapperService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ObjectMapperConfig {

    @Bean
    fun objectMapperService(): ObjectMapperService {
        return ObjectMapperService()
    }
}
