package com.rainist.collectcard.config

import com.github.rainist.idl.apis.external.v1.connect.ConnectGrpc
import com.rainist.common.log.Log
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn

@Configuration
@DependsOn("nettyPidSetting")
class GrpcConfig(
    @Value("\${connect.host}")
    private var connectHost: String,
    @Value("\${connect.port}")
    private var connectPort: Int
) {

    companion object : Log

    @Bean
    fun connectChannel(): ManagedChannel {
        logger.withFieldInfo(Pair("connect channel", "host : $connectHost, port : $connectPort"))
        return ManagedChannelBuilder.forAddress(connectHost, connectPort)
            .usePlaintext()
            .build()
    }

    @Bean
    fun connectStub(connectChannel: ManagedChannel): ConnectGrpc.ConnectBlockingStub {
        return ConnectGrpc.newBlockingStub(connectChannel)
    }
}
