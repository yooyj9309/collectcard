package com.rainist.collectcard.common.meters

interface CollectMeterRegistry {
    fun registerExecutionErrorCount(organizationId: String, executionId: String, apiId: String)
    fun registerServiceErrorCount(serviceName: String)
    fun registerExecutionApiResultCodeCount(organizationId: String, executionId: String, apiId: String, resultCode: String)
}
