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
        executor.corePoolSize = 5
        executor.maxPoolSize = 10
        executor.setQueueCapacity(10)
        executor.keepAliveSeconds = 20
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.setAwaitTerminationSeconds(15)
        return executor
    }
}
