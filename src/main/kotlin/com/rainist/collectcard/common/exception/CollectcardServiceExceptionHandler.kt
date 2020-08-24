package com.rainist.collectcard.common.exception

import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collectcard.common.meters.CollectMeterRegistry
import com.rainist.common.log.Log
import javax.annotation.PostConstruct
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.stereotype.Component

@Component
class CollectcardServiceExceptionHandler(private val collectMeterRegistry: CollectMeterRegistry) {
    @PostConstruct
    fun init() {
        Companion.collectMeterRegistry = this.collectMeterRegistry
    }

    companion object : Log {
        private lateinit var collectMeterRegistry: CollectMeterRegistry

        fun handle(executionContext: ExecutionContext, serviceId: String, serviceName: String, throwable: Throwable) {
            // register meter count
            collectMeterRegistry.registerServiceErrorCount(serviceId)

            // write error log
            logger
                .With("banksaladUserId", executionContext.userId)
                .With("organizationId", executionContext.organizationId)
                .With("serviceId", serviceId)
                .With("serviceName", serviceName)
                .Error("[COLLECT][Service] " +
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
                    throwable
                )
        }
    }
}
