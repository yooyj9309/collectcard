package com.rainist.collectcard.common.exception

import com.rainist.collectcard.common.meters.CollectMeterRegistry
import javax.annotation.PostConstruct
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CollectExecutionExceptionHandler(private val collectMeterRegistry: CollectMeterRegistry) {
    @PostConstruct
    fun init() {
        Companion.collectMeterRegistry = this.collectMeterRegistry
    }

    companion object {
        val log: Logger get() = LoggerFactory.getLogger(this.javaClass)
        private lateinit var collectMeterRegistry: CollectMeterRegistry

        fun handle(organizationId: String, executionId: String, apiId: String, throwable: Throwable) {
            // register meter count
            collectMeterRegistry.registerExecutionErrorCount(organizationId, executionId, apiId)

            // write error log
            log.error("[COLLECT][Execution] executionId: {}\n" +
                    "message: {}\n" +
                    "cause message: {}\n" +
                    "stacktrace: {}",
                executionId,
                throwable.message,
                throwable.cause?.message ?: "",
                ExceptionUtils.getStackTrace(throwable),
                throwable)
        }
    }
}
