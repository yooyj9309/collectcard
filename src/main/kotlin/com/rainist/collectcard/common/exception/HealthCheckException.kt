package com.rainist.collectcard.common.exception

import com.github.banksalad.idl.apis.external.v1.result.ErrorProto
import io.grpc.Status

class HealthCheckException(
    override val message: String = "HealthCheckException",
    override var httpStatusCode: Status.Code = Status.Code.INVALID_ARGUMENT,
    override var errorCode: ErrorProto.ErrorResult.ErrorCode = ErrorProto.ErrorResult.ErrorCode.INTERNAL,
    override var errorMessage: String = "오류가 발생하였습니다.",
    override var errorDescription: String = "오류가 발생하였습니다."
) : GrpcException(message)
