package com.rainist.collectcard.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.rainist.collect.executor.CollectExecutorServiceImpl
import com.rainist.collect.executor.IApiLogger
import com.rainist.collect.executor.IIdGenerator
import com.rainist.collect.executor.ITransferClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CollectConfig(
    private val transferClient: ITransferClient,
    private val idGenerator: IIdGenerator,
    private val apiLogger: IApiLogger
) {

    fun collectObjectMapper(): ObjectMapper {
        val objectMapper = ObjectMapper()
        val jdk8Module = Jdk8Module().configureAbsentsAsNulls(true)
        objectMapper.registerModule(jdk8Module)
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        objectMapper.registerKotlinModule()
        return objectMapper
    }

    @Bean
    fun executorService() = CollectExecutorServiceImpl(transferClient, idGenerator, apiLogger, collectObjectMapper())

    @Bean
    fun collectExecutorService() =
        CollectExecutorServiceImpl(transferClient, idGenerator, apiLogger, collectObjectMapper())
}
