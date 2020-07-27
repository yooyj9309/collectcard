package com.rainist.collectcard.common.meters

interface CollectMeterRegistry {
    fun registerExecutionErrorCount(organizationId: String, executionId: String, apiId: String)
}
