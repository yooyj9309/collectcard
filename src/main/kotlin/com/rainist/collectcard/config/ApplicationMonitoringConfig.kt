package com.rainist.collectcard.config

import com.rainist.common.enumclass.monitoring.MonitoringValue
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.config.MeterFilter
import io.micrometer.core.instrument.config.MeterFilterReply
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationMonitoringConfig {

    @Bean
    fun meterFilter(): MeterFilter {
        return object : MeterFilter {
            override fun accept(id: Meter.Id): MeterFilterReply {
                println(id)
                return MonitoringValue.isTrace(id.name)
            }
        }
    }
}
