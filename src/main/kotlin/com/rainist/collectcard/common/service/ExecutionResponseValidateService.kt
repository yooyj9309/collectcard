package com.rainist.collectcard.common.service

import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collectcard.common.db.entity.ApiLogEntity
import com.rainist.collectcard.common.db.repository.ApiLogRepository
import com.rainist.collectcard.common.enums.ResultCode
import com.rainist.common.log.Log
import com.rainist.common.util.DateTimeUtil
import org.springframework.stereotype.Service

@Service
class ExecutionResponseValidateService(private val apiLogRepository: ApiLogRepository) {

    companion object : Log

    fun validate(
        executionContext: ExecutionContext,
        executionResponse: ExecutionResponse<*>
    ): Boolean {

        var iaResultCodesOk = true

        if (executionResponse.isExceptionOccurred) {
            logError(executionContext.userId, executionContext.organizationId, executionContext.executionRequestId,
                "Exception was occured while execution")
            iaResultCodesOk = false
        }

        val apiLogEntities: List<ApiLogEntity> = apiLogRepository.findByExecutionRequestIdAndCreatedAtBetween(
            executionContext.executionRequestId,
            DateTimeUtil.utcNowLocalDateTime().minusDays(1),
            DateTimeUtil.utcNowLocalDateTime().plusDays(1))

        val resultCodes =
            apiLogEntities.map(ApiLogEntity::resultCode)
                .toMutableList()

        if (resultCodes.contains(ResultCode.EXTERNAL_SERVER_ERROR.name)) {
            logError(executionContext.userId, executionContext.organizationId, executionContext.executionRequestId,
                ResultCode.EXTERNAL_SERVER_ERROR.name)
            iaResultCodesOk = false
        }

        if (resultCodes.contains(ResultCode.INVALID_ACCESS_TOKEN.name)) {
            logError(executionContext.userId, executionContext.organizationId, executionContext.executionRequestId,
                ResultCode.INVALID_ACCESS_TOKEN.name)
            iaResultCodesOk = false
        }

        if (resultCodes.contains(ResultCode.INVALID_USER.name)) {
            logError(executionContext.userId, executionContext.organizationId, executionContext.executionRequestId,
                ResultCode.INVALID_USER.name)
            iaResultCodesOk = false
        }

        if (resultCodes.contains(ResultCode.UNKNOWN.name)) {
            logError(executionContext.userId, executionContext.organizationId, executionContext.executionRequestId,
                ResultCode.UNKNOWN.name)
            iaResultCodesOk = false
        }

        return iaResultCodesOk
    }

    private fun logError(banksaladUserId: String, organizationId: String, executionRequestId: String, message: String) {
        // write error log
        logger
            .With("banksaladUserId", banksaladUserId)
            .With("organizationId", organizationId)
            .With("executionRequestId", executionRequestId)
            .Error("[COLLECT][Service] Result validation fail: " + message +
                    "\norganizationId: " + organizationId
            )
    }

    fun validate(
        executionContext: ExecutionContext,
        executionResponse: List<ExecutionResponse<*>>
    ): Boolean {

        val falseCount = executionResponse.map {
            validate(executionContext, it)
        }
        .filter {
            it == false
        }
        .count()

        return falseCount == 0
    }
}
