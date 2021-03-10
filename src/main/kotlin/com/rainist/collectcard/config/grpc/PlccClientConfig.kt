package com.rainist.collectcard.config.grpc

import com.github.banksalad.idl.apis.v1.plcc.PlccGrpc
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
@Profile(value = ["local", "development", "staging", "production"])
class PlccClientConfig(
    @Value("\${plcc-server.uri}")
    private var plccUri: String,
    val meterRegistry: MeterRegistry
) {

    companion object : Log

    @Value("\${spring.application.name}")
    lateinit var applicationName: String

    @Bean
    fun plccBlockingStub(): PlccGrpc.PlccBlockingStub? {
        val plccChannel = ManagedChannelBuilder.forTarget(plccUri)
            .defaultLoadBalancingPolicy(GrpcConfig.CLIENT_LOAD_BALANCING_POLICY_ROUND_ROBIN)
            .usePlaintext()
            .intercept(StatsUnaryClientInterceptor(meterRegistry, applicationName))
            .build()
        return PlccGrpc.newBlockingStub(plccChannel)
    }
}
