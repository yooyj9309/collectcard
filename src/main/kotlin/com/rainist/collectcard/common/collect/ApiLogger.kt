package com.rainist.collectcard.common.collect

import com.rainist.collect.common.dto.Api
import com.rainist.collect.common.dto.ApiRequest
import com.rainist.collect.common.dto.ApiResponse
import com.rainist.collect.executor.service.IApiLogger
import org.springframework.stereotype.Component

@Component
class ApiLogger : IApiLogger {
    override fun log(id: String, api: Api, apiRequest: ApiRequest<Any>) {
        // TODO : implement api logger
    }

    override fun log(id: String, api: Api, apiResponse: ApiResponse<Any>) {
        // TODO : implement api logger
    }
}
