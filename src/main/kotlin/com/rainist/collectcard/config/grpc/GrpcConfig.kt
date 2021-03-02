package com.rainist.collectcard.config.grpc

import com.github.banksalad.idl.apis.v1.connect.ConnectGrpc
import com.github.banksalad.idl.apis.v1.result.ErrorProto
import com.google.protobuf.StringValue
import com.google.rpc.Status
import com.rainist.collectcard.common.exception.GrpcException
import com.rainist.common.interceptor.StatsUnaryClientInterceptor
import com.rainist.common.log.Log
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn

@Configuration
@DependsOn("nettyPidSetting")
class GrpcConfig(
    @Value("\${connect-server.host}")
    private var connectHost: String,
    @Value("\${connect-server.port}")
    private var connectPort: Int,
    val meterRegistry: MeterRegistry
) {

    companion object : Log {
        const val CLIENT_LOAD_BALANCING_POLICY_ROUND_ROBIN = "round_robin"
    }

    @Value("\${spring.application.name}")
    lateinit var applicationName: String

    @Bean
    fun connectChannel(): ManagedChannel {
        return ManagedChannelBuilder.forAddress(connectHost, connectPort)
            .usePlaintext()
            .intercept(StatsUnaryClientInterceptor(meterRegistry, applicationName))
            .build()
    }

    @Bean
    fun connectStub(connectChannel: ManagedChannel): ConnectGrpc.ConnectBlockingStub {
        return ConnectGrpc.newBlockingStub(connectChannel)
    }
}

/* gRPC exception -> http response 처리 확장 함수 */
fun <V> StreamObserver<V>.onException(t: Throwable) {
    when (t) {
        is GrpcException -> {
            this.onError(t.build())
        }

        else -> {
            val status = Status.newBuilder()
                .setCode(io.grpc.Status.Code.UNKNOWN.value())
                .setMessage(t.message)
                .addDetails(com.google.protobuf.Any.pack(
                    ErrorProto.ErrorResult.newBuilder()
                        .setCode(ErrorProto.ErrorResult.ErrorCode.UNKNOWN)
                        .setMessage("오류가 발생하였습니다.")
                        .setDescription("오류가 발생하였습니다.")
                        .putAllProperties(mutableMapOf())
                        .setClientDisplayError(
                            ErrorProto.ClientDisplayError.newBuilder()
                                .setTitle(StringValue.of("ERROR"))
                                .setMessage("오류가 발생하였습니다.")
                                .setDisplayType(ErrorProto.ClientErrorDisplayType.CLIENT_ERROR_DISPLAY_TYPE_ALERT))
                        .build()))
                .build()

            this.onError(StatusProto.toStatusRuntimeException(status))
        }
    }
}
