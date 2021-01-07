package com.rainist.collectcard.config.grpc

import com.github.banksalad.idl.apis.v1.loan.LoanGrpc
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
class LoanClientConfig(

    @Value("\${loan-server.uri}")
    private var loanUri: String,

    val meterRegistry: MeterRegistry
) {

    companion object : Log

    @Value("\${spring.application.name}")
    lateinit var applicationName: String

    @Bean
    fun loanBlockingStub(): LoanGrpc.LoanBlockingStub? {
        val loanChannel = ManagedChannelBuilder.forTarget(loanUri)
            .defaultLoadBalancingPolicy(GrpcConfig.CLIENT_LOAD_BALANCING_POLICY_ROUND_ROBIN)
            .usePlaintext()
            .intercept(StatsUnaryClientInterceptor(meterRegistry, applicationName))
            .build()
        return LoanGrpc.newBlockingStub(loanChannel)
    }
}
