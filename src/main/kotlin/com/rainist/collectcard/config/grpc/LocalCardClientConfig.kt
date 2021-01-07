package com.rainist.collectcard.config.grpc

import com.github.banksalad.idl.apis.v1.card.CardGrpc
import com.github.banksalad.idl.apis.v1.card.CardGrpc.CardBlockingStub
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
class LocalCardClientConfig(

    @Value("\${card-server.host}")
    private var cardHost: String,

    @Value("\${card-server.port}")
    private var cardPort: Int,

    val meterRegistry: MeterRegistry
) {

    companion object : Log

    @Value("\${spring.application.name}")
    lateinit var applicationName: String

    @Bean
    fun cardChannel(): ManagedChannel {
        return ManagedChannelBuilder.forAddress(cardHost, cardPort)
            .usePlaintext()
            .intercept(StatsUnaryClientInterceptor(meterRegistry, applicationName))
            .build()
    }

    @Bean
    fun cardBlockingStub(@Qualifier("cardChannel") cardChannel: ManagedChannel): CardBlockingStub {
        return CardGrpc.newBlockingStub(cardChannel)
    }
}
