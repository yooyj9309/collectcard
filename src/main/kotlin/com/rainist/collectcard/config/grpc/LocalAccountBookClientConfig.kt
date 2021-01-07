package com.rainist.collectcard.config.grpc

import com.github.banksalad.idl.apis.v1.accountbook.AccountbookGrpc
import com.rainist.common.interceptor.StatsUnaryClientInterceptor
import com.rainist.common.log.Log
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Profile

@Configuration
@DependsOn("nettyPidSetting")
@Profile("local")
class LocalAccountBookClientConfig(

    @Value("\${accountbook-server.host}")
    private var accountBookHost: String,

    @Value("\${accountbook-server.port}")
    private var accountBookPort: Int,

    val meterRegistry: MeterRegistry

) {

    companion object : Log

    @Value("\${spring.application.name}")
    lateinit var applicationName: String

    @Bean
    fun accountBookChannel(): ManagedChannel {
        return ManagedChannelBuilder.forAddress(accountBookHost, accountBookPort)
            .usePlaintext()
            .intercept(StatsUnaryClientInterceptor(meterRegistry, applicationName))
            .build()
    }

    @Bean
    fun accountBookBlockingStub(@Qualifier("accountBookChannel") accountBookChannel: ManagedChannel): AccountbookGrpc.AccountbookBlockingStub {
        return AccountbookGrpc.newBlockingStub(accountBookChannel)
    }
}
