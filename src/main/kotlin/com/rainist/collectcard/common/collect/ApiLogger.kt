package com.rainist.collectcard.common.collect

import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collect.common.log.IApiLogger
import com.rainist.collect.executor.ApiLog
import com.rainist.collectcard.common.service.ApiLogService
import org.springframework.stereotype.Component

@Component
class ApiLogger(private val apiLogService: ApiLogService) : IApiLogger {

    override fun onRequest(context: ExecutionContext, apiLog: ApiLog) {
        val userId: Long = context.userId.toLong()
        apiLogService.logRequest(context.executionRequestId, context.organizationId, userId, apiLog)
    }

    override fun onResponse(context: ExecutionContext, apiLog: ApiLog) {
        val userId: Long = context.userId.toLong()
        apiLogService.logResponse(context.executionRequestId, context.organizationId, userId, apiLog)
    }
}
