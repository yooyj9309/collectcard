package com.rainist.collectcard.common.collect

import com.rainist.collect.common.dto.Api
import com.rainist.collect.common.dto.ApiRequest
import com.rainist.collect.common.dto.ApiResponse
import com.rainist.collect.executor.service.IApiLogger
import com.rainist.common.log.Log
import org.springframework.stereotype.Component

@Component
class ApiLogger : IApiLogger {

    companion object : Log

    override fun log(id: String, api: Api, apiRequest: ApiRequest<Any>) {
        apiRequest.headers.iterator().forEach {
            logger.info("req header : {} , {}", it.key, it.value)
        }
        logger.info("req header : {} , apiRequset : {}", apiRequest.headers, apiRequest.request)
    }

    override fun log(id: String, api: Api, apiResponse: ApiResponse<Any>) {
        apiResponse.headers.iterator().forEach {
            logger.info("res header : {} , {}", it.key, it.value)
        }
        logger.info("res : apiResponse {}", apiResponse.response)
    }
}
