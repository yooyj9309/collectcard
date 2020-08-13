package com.rainist.collectcard.common.util

import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collectcard.common.enums.ResultCode
import com.rainist.common.log.Log

class ExecutionResponseValidator {

    companion object : Log {
        fun validateResponseAndThrow(
            executionResponse: ExecutionResponse<*>,
            resultCodes: List<ResultCode>
        ) {
            if (executionResponse.isExceptionOccurred) {
//                throw CollectcardException(ResultCode.UNKNOWN.name, ResultCode.UNKNOWN.name)
                logger.error("Response validation exception. currently just logging : {} ", ResultCode.UNKNOWN.name)
            }

            if (resultCodes.contains(ResultCode.EXTERNAL_SERVER_ERROR)) {
//                throw CollectcardException(ResultCode.EXTERNAL_SERVER_ERROR.name, ResultCode.EXTERNAL_SERVER_ERROR.name)
                logger.error("Response validation exception. currently just logging : {} ", ResultCode.EXTERNAL_SERVER_ERROR.name)
            }

            if (resultCodes.contains(ResultCode.INVALID_ACCESS_TOKEN)) {
//                throw CollectcardException(ResultCode.INVALID_ACCESS_TOKEN.name, ResultCode.INVALID_ACCESS_TOKEN.name)
                logger.error("Response validation exception. currently just logging : {} ", ResultCode.INVALID_ACCESS_TOKEN.name)
            }

            if (resultCodes.contains(ResultCode.INVALID_USER)) {
//                throw CollectcardException(ResultCode.INVALID_USER.name, ResultCode.INVALID_USER.name)
                logger.error("Response validation exception. currently just logging : {} ", ResultCode.INVALID_USER.name)
            }

            if (resultCodes.contains(ResultCode.UNKNOWN)) {
//                throw CollectcardException(ResultCode.UNKNOWN.name, ResultCode.UNKNOWN.name)
                logger.error("Response validation exception. currently just logging : {} ", ResultCode.UNKNOWN.name)
            }
        }

        fun validateResponseAndThrow(
            executionResponse: ExecutionResponse<*>,
            resultCode: ResultCode?
        ) {
            if (executionResponse.isExceptionOccurred) {
//                throw CollectcardException(ResultCode.UNKNOWN.name, ResultCode.UNKNOWN.name)
                logger.error("Response validation exception. currently just logging : {} ", ResultCode.UNKNOWN.name)
            }

            if (resultCode == ResultCode.EXTERNAL_SERVER_ERROR) {
//                throw CollectcardException(ResultCode.EXTERNAL_SERVER_ERROR.name, ResultCode.EXTERNAL_SERVER_ERROR.name)
                logger.error("Response validation exception. currently just logging : {} ", ResultCode.EXTERNAL_SERVER_ERROR.name)
            }

            if (resultCode == ResultCode.INVALID_ACCESS_TOKEN) {
//                throw CollectcardException(ResultCode.INVALID_ACCESS_TOKEN.name, ResultCode.INVALID_ACCESS_TOKEN.name)
                logger.error("Response validation exception. currently just logging : {} ", ResultCode.INVALID_ACCESS_TOKEN.name)
            }

            if (resultCode == ResultCode.INVALID_USER) {
//                throw CollectcardException(ResultCode.INVALID_USER.name, ResultCode.INVALID_USER.name)
                logger.error("Response validation exception. currently just logging : {} ", ResultCode.INVALID_USER.name)
            }

            if (resultCode == ResultCode.UNKNOWN) {
//                throw CollectcardException(ResultCode.UNKNOWN.name, ResultCode.UNKNOWN.name)
                logger.error("Response validation exception. currently just logging : {} ", ResultCode.UNKNOWN.name)
            }
        }
    }
}
