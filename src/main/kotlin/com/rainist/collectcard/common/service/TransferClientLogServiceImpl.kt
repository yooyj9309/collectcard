package com.rainist.collectcard.common.service

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import java.net.URI
import java.util.concurrent.TimeUnit
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class TransferClientLogServiceImpl(
    val registry: MeterRegistry
) : TransferClientLogService {

    companion object {
        const val TRANSFER_ALL_TIMEOUT = "transfer.all.timeout"
        const val TRANSFER_ALL_FAILURE = "transfer.all.failure"
        const val TRANSFER_ALL_UNKNOWN = "transfer.all.unknown"
        const val TRANSFER_ALL_SUCCESS = "transfer.all.success"
        const val TRANSFER_ALL_TIMING = "transfer.all.timing"
    }

    @Value("\${spring.profiles.active}")
    lateinit var activeName: String

    @Value("\${spring.application.name}")
    lateinit var applicationName: String

    override fun loggingTimeoutCount(uri: URI) {
        val tags = Tags
            .of("url", uri.toString())
            .and("url_host", uri.host)
            .and("url_path", uri.path)

        registry.counter("$activeName.$applicationName.$TRANSFER_ALL_TIMEOUT", tags).increment()
    }

    override fun loggingFailureCount(uri: URI, httpStatus: HttpStatus) {
        val tags = Tags
            .of("url", uri.toString())
            .and("code", httpStatus.value().toString())
            .and("url_host", uri.host)
            .and("url_path", uri.path)

        registry.counter("$activeName.$applicationName.$TRANSFER_ALL_FAILURE", tags).increment()
    }

    override fun loggingUnknownErrorCount(uri: URI) {
        val tags = Tags
            .of("url", uri.toString())
            .and("url_host", uri.host)
            .and("url_path", uri.path)

        registry.counter("$activeName.$applicationName.$TRANSFER_ALL_UNKNOWN", tags).increment()
    }

    override fun loggingSuccessCount(uri: URI, httpStatus: HttpStatus) {
        val tags = Tags
            .of("url", uri.toString())
            .and("code", httpStatus.value().toString())
            .and("url_host", uri.host)
            .and("url_path", uri.path)

        registry.counter("$activeName.$applicationName.$TRANSFER_ALL_SUCCESS", tags).increment()
    }

    override fun loggingTiming(uri: URI, totalTimeMillis: Long) {
        val tags = Tags
            .of("url", uri.toString())
            .and("url_host", uri.host)
            .and("url_path", uri.path)

        registry.timer("$activeName.$applicationName.$TRANSFER_ALL_TIMING", tags).record(totalTimeMillis, TimeUnit.MILLISECONDS)
    }
}
