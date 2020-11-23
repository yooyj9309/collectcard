package com.rainist.collectcard.config.grpc

import com.github.banksalad.idl.apis.v1.cipher.CipherGrpc
import com.github.banksalad.idl.apis.v1.cipher.CipherGrpc.CipherBlockingStub
import com.rainist.collectcard.config.grpc.GrpcConfig.Companion.CLIENT_LOAD_BALANCING_POLICY_ROUND_ROBIN
import com.rainist.common.interceptor.StatsUnaryClientInterceptor
import com.rainist.common.log.Log
import io.grpc.ManagedChannelBuilder
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Profile

@Configuration
@DependsOn("nettyPidSetting")
@Profile(value = ["development", "staging", "production"])
class CipherClientConfig(

    @Value("\${cipher-server.uri}")
    private var cipherUri: String,

    val meterRegistry: MeterRegistry
) {

    companion object : Log

    @Value("\${spring.application.name}")
    lateinit var applicationName: String

    @Bean
    fun cipherBlockingStub(): CipherBlockingStub? {
        val cipherChannel = ManagedChannelBuilder.forTarget(cipherUri)
            .defaultLoadBalancingPolicy(CLIENT_LOAD_BALANCING_POLICY_ROUND_ROBIN)
            .usePlaintext()
            .intercept(StatsUnaryClientInterceptor(meterRegistry, applicationName))
            .build()
        return CipherGrpc.newBlockingStub(cipherChannel)
    }
}
