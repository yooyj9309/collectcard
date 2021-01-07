package com.rainist.collectcard.config.grpc

import com.github.banksalad.idl.apis.v1.loan.LoanGrpc
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
class LocalLoanClientConfig(

    @Value("\${loan-server.host}")
    private var loanHost: String,

    @Value("\${loan-server.port}")
    private var loanPort: Int,

    val meterRegistry: MeterRegistry

) {

    companion object : Log

    @Value("\${spring.application.name}")
    lateinit var applicationName: String

    @Bean
    fun loanChannel(): ManagedChannel {
        return ManagedChannelBuilder.forAddress(loanHost, loanPort)
            .usePlaintext()
            .intercept(StatsUnaryClientInterceptor(meterRegistry, applicationName))
            .build()
    }

    @Bean
    fun loanBlockingStub(@Qualifier("loanChannel") loanChannel: ManagedChannel): LoanGrpc.LoanBlockingStub? {
        return LoanGrpc.newBlockingStub(loanChannel)
    }
}
