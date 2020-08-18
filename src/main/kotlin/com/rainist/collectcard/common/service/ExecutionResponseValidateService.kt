package com.rainist.collectcard.common.service

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
        executionRequestId: String,
        executionResponse: ExecutionResponse<*>
    ): Boolean {

        if (executionResponse.isExceptionOccurred) {
            logger.error("Response validation exception. exception was occured while execution")
            return false
        }

        val apiLogEntities: List<ApiLogEntity> = apiLogRepository.findByExecutionRequestIdAndCreatedAtBetween(
            executionRequestId,
            DateTimeUtil.utcNowLocalDateTime().minusDays(1),
            DateTimeUtil.utcNowLocalDateTime().plusDays(1))

        val resultCodes =
            apiLogEntities.map(ApiLogEntity::responseCode)
            .toMutableList()

        if (resultCodes.contains(ResultCode.EXTERNAL_SERVER_ERROR.name)) {
            logger.error("Response validation exception. currently just logging : {} ", ResultCode.EXTERNAL_SERVER_ERROR.name)
            return false
        }

        if (resultCodes.contains(ResultCode.INVALID_ACCESS_TOKEN.name)) {
            logger.error("Response validation exception. currently just logging : {} ", ResultCode.INVALID_ACCESS_TOKEN.name)
            return false
        }

        if (resultCodes.contains(ResultCode.INVALID_USER.name)) {
            logger.error("Response validation exception. currently just logging : {} ", ResultCode.INVALID_USER.name)
            return false
        }

        if (resultCodes.contains(ResultCode.UNKNOWN.name)) {
            logger.error("Response validation exception. currently just logging : {} ", ResultCode.UNKNOWN.name)
            return false
        }

        return true
    }
}
