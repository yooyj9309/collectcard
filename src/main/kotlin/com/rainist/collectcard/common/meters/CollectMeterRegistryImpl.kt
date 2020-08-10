package com.rainist.collectcard.common.meters

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

private const val TAG_ORGANIZATION_ID = "organizationId"
private const val TAG_EXECUTION_ID = "executionId"
private const val TAG_API_ID = "apiId"

private const val TAG_SERVICE_NAME = "serviceName"

private const val COLLECT_EXECUTION_ERROR_COUNT = "collect.execution.error.count"
private const val COLLECT_SERVICE_ERROR_COUNT = "collect.service.error.count"

@Service
class CollectMeterRegistryImpl(private val meterRegistry: MeterRegistry) : CollectMeterRegistry {
    @Value("\${spring.profiles.active}")
    lateinit var activeName: String

    @Value("\${spring.application.name}")
    lateinit var applicationName: String

    private var executionErrorCountName: String? = null
    private var serviceErrorCountName: String? = null

    @PostConstruct
    fun init() {
        executionErrorCountName = "$activeName.$applicationName.$COLLECT_EXECUTION_ERROR_COUNT"
        serviceErrorCountName = "$activeName.$applicationName.$COLLECT_SERVICE_ERROR_COUNT"
    }

    override fun registerExecutionErrorCount(organizationId: String, executionId: String, apiId: String) {
        val tags = Tags.of(TAG_ORGANIZATION_ID, organizationId)
            .and(TAG_EXECUTION_ID, executionId)
            .and(TAG_API_ID, apiId)

        meterRegistry.counter(executionErrorCountName ?: "", tags).increment()
    }

    override fun registerServiceErrorCount(serviceName: String) {
        val tags = Tags.of(TAG_SERVICE_NAME, serviceName)

        meterRegistry.counter(serviceErrorCountName ?: "", tags).increment()
    }
}
