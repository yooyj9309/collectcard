package com.rainist.collectcard.common.collect.execution

import com.rainist.collectcard.common.meters.CollectMeterRegistry
import javax.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ExecutionExceptionHandler(private val collectMeterRegistry: CollectMeterRegistry) {
    @PostConstruct
    fun init() {
        ExecutionExceptionHandler.collectMeterRegistry = this.collectMeterRegistry
    }

    companion object {
        val log: Logger get() = LoggerFactory.getLogger(this.javaClass)
        lateinit var collectMeterRegistry: CollectMeterRegistry

        fun handle(organizationId: String, executionId: String, apiId: String, throwable: Throwable) {
            collectMeterRegistry.registerExecutionErrorCount(organizationId, executionId, apiId)
            log.error("[COLLECT][Execution] {} : {}", executionId, throwable.message)
        }
    }
}
