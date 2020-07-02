package com.rainist.collectcard.userinfo.dto

data class UserInfoResponse(
    var dataHeader: UserInfoResponseDataHeader? = null,
    var dataBody: UserInfoResponseDataBody? = null
)

data class UserInfoResponseDataHeader(
    var successCode: String? = null,
    var resultCode: String? = null,
    var resultMessage: String? = null
)

data class UserInfoResponseDataBody(
    var userInfo: UserInfo? = null
)
