package com.rainist.collectcard.config

import com.rainist.common.interceptor.StatsUnaryServerInterceptor
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.config.NamingConvention
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MicrometerConfig {

    @Autowired
    lateinit var meterRegistry: MeterRegistry

    @PostConstruct
    fun init() {
        meterRegistry.Config().namingConvention(NamingConvention.dot)
    }

    @Bean
    fun statsUnaryServerInterceptor(): StatsUnaryServerInterceptor {
        return StatsUnaryServerInterceptor(meterRegistry)
    }
}
