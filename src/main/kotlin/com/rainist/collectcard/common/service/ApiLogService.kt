package com.rainist.collectcard.common.service

import com.rainist.collect.executor.ApiLog

interface ApiLogService {
    fun logRequest(organizationId: String, banksaladUserId: Long, apiLog: ApiLog)

    fun logResponse(organizationId: String, banksaladUserId: Long, apiLog: ApiLog)
}
