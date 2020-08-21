package com.rainist.collectcard.config

import com.rainist.common.interceptor.StatsUnaryServerInterceptor
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.config.NamingConvention
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
class MicrometerConfig {

    @Autowired
    lateinit var meterRegistry: MeterRegistry

    @Autowired
    lateinit var customConvention: CustomConvention

    @PostConstruct
    fun init() {
        meterRegistry.Config().namingConvention(customConvention)
    }

    @Bean
    fun statsUnaryServerInterceptor(): StatsUnaryServerInterceptor {
        return StatsUnaryServerInterceptor(meterRegistry)
    }
}

@Component
class CustomConvention : NamingConvention {

    @Value("\${spring.profiles.active}")
    lateinit var activeName: String

    @Value("\${spring.application.name}")
    lateinit var applicationName: String

    override fun name(name: String, type: Meter.Type, baseUnit: String?): String {
        return "$activeName.$applicationName.$name"
    }
}
