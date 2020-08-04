package com.rainist.collectcard.common.service

import com.rainist.collect.executor.ApiLog
import com.rainist.collectcard.common.db.entity.ApiLogEntity
import com.rainist.collectcard.common.db.repository.ApiLogRepository
import java.time.LocalDateTime
import java.time.ZoneId
import org.springframework.stereotype.Service

@Service
class ApiLogServiceImpl(private val apiLogRepository: ApiLogRepository) : ApiLogService {
    companion object {
        val ZONE_ID_UTC = "UTC"
    }

    override fun logRequest(organizationId: String, banksaladUserId: Long, apiLog: ApiLog) {
        apiLogRepository.save(
            ApiLogEntity().apply {
                this.requestId = apiLog.id
                this.organizationId = organizationId
                this.banksaladUserId = banksaladUserId

                this.apiId = apiLog.api.id
                this.requestUrl = apiLog.api.endpoint
                this.httpMethod = apiLog.api.method.name
                this.organizationApiId = apiLog.api.name

                this.requestHeaderText = apiLog.request?.header ?: ""
                this.requestBodyText = apiLog.request?.body ?: ""

                this.transformedRequestHeaderText = apiLog.request?.transformedHeader ?: ""
                this.transformedRequestBodyText = apiLog.request?.transformedBody ?: ""

                this.requestDatetime = LocalDateTime.now(ZoneId.of(ZONE_ID_UTC))
            }
        )
    }

    override fun logResponse(organizationId: String, banksaladUserId: Long, apiLog: ApiLog) {
        val apiLogEntity = apiLogRepository.findByRequestId(apiLog.id)
            ?: ApiLogEntity().apply {
                this.organizationId = organizationId
                this.banksaladUserId = banksaladUserId
                this.apiId = apiLog.api.id
                this.requestUrl = apiLog.api.endpoint
                this.httpMethod = apiLog.api.method.name
                this.organizationApiId = apiLog.api.name
            }

        apiLogEntity.apply {
            this.responseCode = apiLog.response?.responseCode
            // TODO jayden-lee resultCode java-banksalad Response 객체 프로퍼티에 추가해야함
            this.resultCode = ""

            this.responseHeaderText = apiLog.response?.header
            this.responseBodyText = apiLog.response?.body

            this.transformedResponseHeaderText = apiLog.response?.transformedHeader
            this.transformedResponseBodyText = apiLog.response?.transformedBody

            this.responseDatetime = LocalDateTime.now(ZoneId.of(ZONE_ID_UTC))
        }

        apiLogRepository.save(apiLogEntity)
    }
}
