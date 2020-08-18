package com.rainist.collectcard.config

import java.util.concurrent.Executor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
class ThreadPoolConfig {

    @Bean(name = ["async-thread"])
    fun threadPoolConfig(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.setThreadNamePrefix("Async-Thread-")
        executor.corePoolSize = 10
        executor.maxPoolSize = 20
        executor.setQueueCapacity(1000)
        executor.keepAliveSeconds = 20
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.setAwaitTerminationSeconds(15)
        executor.initialize()
        return executor
    }
}
