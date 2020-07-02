package com.rainist.collectcard.userinfo.dto

import com.rainist.collectcard.common.dto.Empty

data class UserInfoRequest(
    var dataHeader: UserInfoRequestDataHeader? = null,
    var dataBody: UserInfoRequestDataBody? = null
)

data class UserInfoRequestDataHeader(
    var empty: Any = Empty()
)

data class UserInfoRequestDataBody(
    var empty: Any = Empty()
)
