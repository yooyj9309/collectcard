package com.rainist.collectcard.common.exception

import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collectcard.common.exception.CollectcardServiceExceptionHandler.Companion.Error
import com.rainist.collectcard.common.exception.CollectcardServiceExceptionHandler.Companion.With
import com.rainist.collectcard.common.meters.CollectMeterRegistry
import com.rainist.common.log.Log
import javax.annotation.PostConstruct
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.stereotype.Component

@Component
class CollectExecutionExceptionHandler(private val collectMeterRegistry: CollectMeterRegistry) {
    @PostConstruct
    fun init() {
        Companion.collectMeterRegistry = this.collectMeterRegistry
    }

    companion object : Log {
        private lateinit var collectMeterRegistry: CollectMeterRegistry

        fun handle(executionContext: ExecutionContext, organizationId: String, executionId: String, apiId: String, throwable: Throwable) {
            // register meter count
            collectMeterRegistry.registerExecutionErrorCount(organizationId, executionId, apiId)

            // write error log
            logger
                .With("banksaladUserId", executionContext.userId)
                .With("organizationId", executionContext.organizationId)
                .Error("[COLLECT][Service] " +
                        "banksaladUserId: {}\n" +
                        "organizationId: {}\n" +
                        "message: {}\n" +
                        "cause message: {}\n" +
                        "stacktrace: {}",
                    executionContext.userId,
                    executionContext.organizationId,
                    throwable.message,
                    throwable.cause?.message ?: "",
                    ExceptionUtils.getStackTrace(throwable),
                    throwable
                )
        }
    }
}
