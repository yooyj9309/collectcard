package com.rainist.collectcard.common.exception

import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collectcard.common.meters.CollectMeterRegistry
import javax.annotation.PostConstruct
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CollectcardServiceExceptionHandler(private val collectMeterRegistry: CollectMeterRegistry) {
    @PostConstruct
    fun init() {
        Companion.collectMeterRegistry = this.collectMeterRegistry
    }

    companion object {
        val log: Logger get() = LoggerFactory.getLogger(this.javaClass)
        private lateinit var collectMeterRegistry: CollectMeterRegistry

        fun handle(serviceId: String, serviceName: String, throwable: Throwable) {
            // register meter count
            collectMeterRegistry.registerServiceErrorCount(serviceId)

            // write error log
            log.error("[COLLECT][Service] serviceId: {}\n" +
                    "serviceName: {}\n" +
                    "message: {}\n" +
                    "cause message: {}\n" +
                    "stacktrace: {}",
                serviceId,
                serviceName,
                throwable.message,
                throwable.cause?.message ?: "",
                ExceptionUtils.getStackTrace(throwable),
                throwable)
        }

        fun handle(executionContext: ExecutionContext, serviceId: String, serviceName: String, throwable: Throwable) {
            // register meter count
            collectMeterRegistry.registerServiceErrorCount(serviceId)

            // write error log
            log.error("[COLLECT][Service] " +
                    "banksaladUserId: {}\n" +
                    "organizationId: {}\n" +
                    "serviceId: {}\n" +
                    "serviceName: {}\n" +
                    "message: {}\n" +
                    "cause message: {}\n" +
                    "stacktrace: {}",
                executionContext.userId,
                executionContext.organizationId,
                serviceId,
                serviceName,
                throwable.message,
                throwable.cause?.message ?: "",
                ExceptionUtils.getStackTrace(throwable),
                throwable)
        }
    }
}
