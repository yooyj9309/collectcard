package com.rainist.collectcard.userinfo

import com.rainist.collectcard.userinfo.dto.UserInfoRequest
import com.rainist.collectcard.userinfo.dto.UserInfoResponse

interface UserInfoService {
    fun getUserInfo(userInfoRequest: UserInfoRequest): UserInfoResponse?
}
