package com.rainist.collectcard.config.grpc

import com.github.banksalad.idl.apis.v1.cipher.CipherGrpc
import com.github.banksalad.idl.apis.v1.cipher.CipherGrpc.CipherBlockingStub
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
class LocalCipherClientConfig(

    @Value("\${cipher-server.host}")
    private var cipherHost: String,

    @Value("\${cipher-server.port}")
    private var cipherPort: Int,

    val meterRegistry: MeterRegistry
) {

    companion object : Log

    @Value("\${spring.application.name}")
    lateinit var applicationName: String

    @Bean
    fun cipherChannel(): ManagedChannel {
        return ManagedChannelBuilder.forAddress(cipherHost, cipherPort)
            .usePlaintext()
            .intercept(StatsUnaryClientInterceptor(meterRegistry, applicationName))
            .build()
    }

    @Bean
    fun cipherBlockingStub(@Qualifier("cipherChannel") cipherChannel: ManagedChannel): CipherGrpc.CipherBlockingStub {
        return CipherGrpc.newBlockingStub(cipherChannel)
    }
}
