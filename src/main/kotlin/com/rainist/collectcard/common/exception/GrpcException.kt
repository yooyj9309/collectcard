package com.rainist.collectcard.common.exception

import com.github.rainist.idl.apis.external.v1.result.ErrorProto
import com.google.rpc.Status
import io.grpc.Status.Code
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import java.lang.RuntimeException

/**
 *   http Status 500 Internal Server Error { httpStatusCode }
 *   Json body
    {
        "code": "{errorCode}",
        "message": "{errorMessage}",
        "description": "{errorDescription}",
        "properties":{
            "{pair.key}" , "{pair.value}"
        },
        "client_display_error":{
            "title": {clientDisplayErrorBuilder setTitle},
            "message": "{clientDisplayErrorBuilder setMessage}",
            "display_type": "{clientDisplayErrorBuilder setDisplayType}"
        }
    }
 */
open class GrpcException(
    override val message: String = "GrpcException",

    // http response status
    open var httpStatusCode: Code = Code.UNKNOWN,

    // json body error object
    open var errorCode: ErrorProto.ErrorResult.ErrorCode = ErrorProto.ErrorResult.ErrorCode.UNKNOWN,
    open var errorMessage: String = "",
    open var errorDescription: String = "",

    // properties object
    open var properties: MutableMap<String, String> = mutableMapOf(),

    // client display info
    open var clientDisplayError: ErrorProto.ClientDisplayError = ErrorProto.ClientDisplayError.getDefaultInstance()

) : RuntimeException(message) {

    fun addProperties(pair: Pair<String, String>) {
        this.properties[pair.first] = pair.second
    }

    fun build(): StatusRuntimeException? {
        val status = Status.newBuilder()
            .setCode(this.httpStatusCode.value())
            .setMessage(this.message)
            .addDetails(
                com.google.protobuf.Any.pack(
                    ErrorProto.ErrorResult.newBuilder()
                        .setCode(this.errorCode)
                        .setMessage(this.errorMessage)
                        .setDescription(this.errorDescription)
                        .putAllProperties(this.properties)
                        .setClientDisplayError(this.clientDisplayError)
                        .build()
                )
            )
            .build()

        return StatusProto.toStatusRuntimeException(status)
    }
}
