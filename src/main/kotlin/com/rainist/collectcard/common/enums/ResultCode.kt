package com.rainist.collectcard.common.enums

enum class ResultCode(
    var index: Int,
    var description: String
) {
    OK(0, "성공"),
    INVALID_USER(1, "wrong ci"),
    INVALID_ACCESS_TOKEN(2, "wrong token"),
    EXTERNAL_SERVER_ERROR(3, "error"),
    INVALID_PARAMETER(4, "wrong parameter"),
    UNKNOWN(99, "unknown")
}
