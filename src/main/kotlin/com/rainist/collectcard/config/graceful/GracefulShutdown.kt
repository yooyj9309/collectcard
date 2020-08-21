package com.rainist.collectcard.config.graceful

import com.rainist.common.log.Log
import java.lang.Thread.sleep
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextClosedEvent
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component

@Component
class GracefulShutdown(
    val applicationContext: ApplicationContext

) : ApplicationListener<ContextClosedEvent> {

    companion object : Log

    override fun onApplicationEvent(event: ContextClosedEvent) {

        val executors = applicationContext.getBeansOfType(ThreadPoolTaskExecutor::class.java)

        executors.values.forEach { threadPool ->
            var retryCount = 0

            // banksalad gateway : 15초, spring boot gRPC : 20초, spring boot application : 20초
            while (threadPool.activeCount > 0 && ++retryCount < 20) {
                sleep(1000)
            }

            if (retryCount >= 20) {
                logger.error("GracefulShutdown Error threadName : {}, activeCount : {}", threadPool.threadNamePrefix, threadPool.activeCount)
            }

            threadPool.shutdown()
        }
    }
}
