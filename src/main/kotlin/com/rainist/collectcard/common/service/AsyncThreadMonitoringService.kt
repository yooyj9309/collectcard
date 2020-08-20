package com.rainist.collectcard.common.service

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import kotlin.concurrent.timer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Service

@Service
class AsyncThreadMonitoringService(
    @Qualifier("collect-thread") val collectExecutor: Executor,
    @Qualifier("async-thread") val asyncExecutor: Executor,
    val meterRegistry: MeterRegistry
) {
    companion object {
        const val ASYNC_THREAD_QUEUE_SIZE = "async.thread.queue.size"
        const val ASYNC_THREAD_ACTIVE_COUNT = "async.thread.active.count"
    }

    @Value("\${spring.profiles.active}")
    lateinit var activeName: String

    @Value("\${spring.application.name}")
    lateinit var applicationName: String

    @PostConstruct
    fun init() {
        val threadList = mutableListOf(
            collectExecutor as ThreadPoolTaskExecutor,
            asyncExecutor as ThreadPoolTaskExecutor
        )

        threadList.forEach { threadPool ->
            ThreadMonitoringTask(
                threadPool,
                "$activeName.$applicationName",
                TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS),
                meterRegistry
            ).start()
        }
    }
}

class ThreadMonitoringTask(
    private val threadPool: ThreadPoolTaskExecutor,
    private val measurement: String,
    private val interval: Long,
    private val meterRegistry: MeterRegistry
) {

    fun start() {
        val tags = Tags.of("thread_name", threadPool.threadNamePrefix)

        timer(period = interval) {
            val queueSize = threadPool.threadPoolExecutor.queue.size.toDouble()
            val activeCount = threadPool.activeCount.toDouble()
            meterRegistry.counter("$measurement.${AsyncThreadMonitoringService.ASYNC_THREAD_ACTIVE_COUNT}", tags).increment(activeCount)
            meterRegistry.counter("$measurement.${AsyncThreadMonitoringService.ASYNC_THREAD_QUEUE_SIZE}", tags).increment(queueSize)
        }
    }
}
