package com.rainist.collectcard.userinfo.dto

class UserInfoResponse(
    var dataHeader: UserInfoResponseDataHeader? = null,
    var dataBody: UserInfoResponseDataBody? = null
)

class UserInfoResponseDataHeader(
    var successCode: String? = null,
    var resultCode: String? = null,
    var resultMessage: String? = null
)

class UserInfoResponseDataBody(
    var userInfo: UserInfo? = null
)
