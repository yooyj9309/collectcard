package com.rainist.collectcard.common.service

import com.rainist.collect.executor.ApiLog

interface ApiLogService {
    fun logRequest(executionRequestId: String, organizationId: String, banksaladUserId: Long, apiLog: ApiLog)

    fun logResponse(executionRequestId: String, organizationId: String, banksaladUserId: Long, apiLog: ApiLog)
}
