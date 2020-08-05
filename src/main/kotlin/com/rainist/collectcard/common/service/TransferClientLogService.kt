package com.rainist.collectcard.common.service

import java.net.URI
import org.springframework.http.HttpStatus

interface TransferClientLogService {

    fun loggingTimeoutCount(uri: URI)
    fun loggingFailureCount(uri: URI, httpStatus: HttpStatus)
    fun loggingUnknownErrorCount(uri: URI)
    fun loggingSuccessCount(uri: URI, httpStatus: HttpStatus)
    fun loggingTiming(uri: URI, totalTimeMillis: Long)
}
