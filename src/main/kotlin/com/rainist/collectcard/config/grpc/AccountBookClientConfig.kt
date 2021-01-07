package com.rainist.collectcard.config.grpc

import com.github.banksalad.idl.apis.v1.accountbook.AccountbookGrpc
import com.rainist.common.interceptor.StatsUnaryClientInterceptor
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
class AccountBookClientConfig(

    @Value("\${accountbook-server.uri}")
    private var accountBookUri: String,

    val meterRegistry: MeterRegistry
) {

    companion object : com.rainist.common.log.Log

    @Value("\${spring.application.name}")
    lateinit var applicationName: String

    @Bean
    fun accountBookBlockingStub(): AccountbookGrpc.AccountbookBlockingStub? {
        val accountBookChannel = ManagedChannelBuilder.forTarget(accountBookUri)
            .defaultLoadBalancingPolicy(GrpcConfig.CLIENT_LOAD_BALANCING_POLICY_ROUND_ROBIN)
            .usePlaintext()
            .intercept(StatsUnaryClientInterceptor(meterRegistry, applicationName))
            .build()
        return AccountbookGrpc.newBlockingStub(accountBookChannel)
    }
}
