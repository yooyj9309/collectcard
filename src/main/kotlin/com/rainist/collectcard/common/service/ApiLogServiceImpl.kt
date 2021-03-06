package com.rainist.collectcard.common.service

import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import com.rainist.collect.executor.ApiLog
import com.rainist.collectcard.common.db.entity.ApiLogEntity
import com.rainist.collectcard.common.db.repository.ApiLogRepository
import com.rainist.collectcard.common.meters.CollectMeterRegistry
import com.rainist.common.util.DateTimeUtil
import java.time.LocalDateTime
import java.time.ZoneId
import org.springframework.stereotype.Service

@Service
class ApiLogServiceImpl(
    private val apiLogRepository: ApiLogRepository,
    private val collectMeterRegistry: CollectMeterRegistry
) : ApiLogService {

    companion object {
        val ZONE_ID_UTC = "UTC"
    }

    override fun logRequest(executionRequestId: String, organizationId: String, banksaladUserId: Long, apiLog: ApiLog) {
        apiLogRepository.save(
            ApiLogEntity().apply {
                this.executionRequestId = executionRequestId
                this.apiRequestId = apiLog.id
                this.organizationId = organizationId
                this.banksaladUserId = banksaladUserId

                this.apiId = apiLog.api.id
                this.requestUrl = apiLog.api.endpoint
                this.httpMethod = apiLog.api.method.name
                this.organizationApiId = apiLog.api.name

                this.requestHeader = apiLog.request?.header ?: ""
                this.requestBody = apiLog.request?.body ?: ""

                this.transformedRequestHeader = apiLog.request?.transformedHeader ?: ""
                this.transformedRequestBody = apiLog.request?.transformedBody ?: ""

                this.requestDatetime = LocalDateTime.now(ZoneId.of(ZONE_ID_UTC))
            }
        )
    }

    override fun logResponse(executionRequestId: String, organizationId: String, banksaladUserId: Long, apiLog: ApiLog) {

        val resultCodeAndMessage = parseResultCodeAndMessage(apiLog.response?.transformedBody)

        val apiLogEntity = apiLogRepository.findByExecutionRequestIdAndApiRequestIdAndCreatedAtBetween(
                executionRequestId,
                apiLog.id,
                DateTimeUtil.utcNowLocalDateTime().minusDays(1),
                DateTimeUtil.utcNowLocalDateTime().plusDays(1)
            ) ?: ApiLogEntity().apply {
                this.executionRequestId = executionRequestId
                this.organizationId = organizationId
                this.banksaladUserId = banksaladUserId
                this.apiId = apiLog.api.id
                this.requestUrl = apiLog.api.endpoint
                this.httpMethod = apiLog.api.method.name
                this.organizationApiId = apiLog.api.name

                this.apiRequestId = ""
                this.requestHeader = ""
                this.requestBody = ""

                this.transformedRequestHeader = ""
                this.transformedRequestBody = ""

                this.requestDatetime = LocalDateTime.now(ZoneId.of(ZONE_ID_UTC))
            }

        apiLogEntity.apply {
            this.responseCode = apiLog.response?.responseCode
            // TODO jayden-lee resultCode java-banksalad Response ?????? ??????????????? ???????????????
            this.resultCode = resultCodeAndMessage.first
            this.resultMessage = resultCodeAndMessage.second
            this.responseHeader = apiLog.response?.header
            this.responseBody = apiLog.response?.body

            this.transformedResponseHeader = apiLog.response?.transformedHeader
            this.transformedResponseBody = apiLog.response?.transformedBody

            this.responseDatetime = LocalDateTime.now(ZoneId.of(ZONE_ID_UTC))
        }

        apiLogRepository.save(apiLogEntity)

        /* count result_code */
        collectMeterRegistry.registerExecutionApiResultCodeCount(
            organizationId = apiLogEntity.organizationId ?: "",
            executionId = "",
            apiId = apiLogEntity.apiId ?: "",
            resultCode = apiLogEntity.resultCode ?: ""
        )
    }

    fun parseResultCodeAndMessage(json: String?): Pair<String, String> {
        return if (! json.isNullOrBlank()) {
            val resultCode = try {
                JsonPath.parse(json).read("\$.dataHeader.resultCode", String::class.java)
            } catch (e: PathNotFoundException) {
                ""
            }

            val resultMessage = try {
                JsonPath.parse(json).read("\$.dataHeader.resultMessage", String::class.java)
            } catch (e: PathNotFoundException) {
                ""
            }

            Pair(resultCode, resultMessage)
        } else {
            Pair("", "")
        }
    }
}
