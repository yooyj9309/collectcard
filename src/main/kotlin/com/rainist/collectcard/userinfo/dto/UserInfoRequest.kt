package com.rainist.collectcard.userinfo.dto

import com.rainist.collectcard.common.dto.Empty
import javax.validation.constraints.NotEmpty

data class UserInfoRequest(
    @field:NotEmpty
    var banksaladUserId: String? = null,

    @field:NotEmpty
    var organizationObjectid: String? = null,

    var dataHeader: UserInfoRequestDataHeader? = null,
    var dataBody: UserInfoRequestDataBody? = null
)

data class UserInfoRequestDataHeader(
    var empty: Any = Empty()
)

data class UserInfoRequestDataBody(
    var empty: Any = Empty()
)
