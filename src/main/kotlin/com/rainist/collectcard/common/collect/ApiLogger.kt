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
        logger.info("req apiRequset : {}", apiRequest.request)
    }

    override fun log(id: String, api: Api, apiResponse: ApiResponse<Any>) {
        logger.info("res : apiResponse {}", apiResponse.response)
    }
}
