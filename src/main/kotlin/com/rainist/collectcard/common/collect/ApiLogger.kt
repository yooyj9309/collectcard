package com.rainist.collectcard.common.collect

import com.rainist.collect.common.api.Api
import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.IApiLogger
import com.rainist.common.log.Log
import org.springframework.stereotype.Component

@Component
class ApiLogger : IApiLogger {

    companion object : Log

    override fun log(id: String, api: Api, ExecutionRequest: ExecutionRequest<Any>) {
        ExecutionRequest.headers.iterator().forEach {
            logger.info("req header : {} , {}", it.key, it.value)
        }
        logger.info("res body : {}", ExecutionRequest.request)
    }

    override fun log(id: String, api: Api, ExecutionResponse: ExecutionResponse<Any>) {
        ExecutionResponse.headers.iterator().forEach {
            logger.info("res header : {}, {}", it.key, it.value)
        }
        logger.info("res body {}", ExecutionResponse.response)
    }
}
