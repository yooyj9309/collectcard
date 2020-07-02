package com.rainist.collectcard.userinfo

import com.rainist.collect.common.dto.ApiRequest
import com.rainist.collect.common.dto.ApiResponse
import com.rainist.collect.executor.service.CollectExecutorService
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.header.HeaderService
import com.rainist.collectcard.header.dto.HeaderInfo
import com.rainist.collectcard.userinfo.dto.UserInfoRequest
import com.rainist.collectcard.userinfo.dto.UserInfoResponse
import com.rainist.common.service.ValidationService
import org.springframework.stereotype.Service

@Service
class UserInfoServiceImpl(
    val validationService: ValidationService,
    val headerService: HeaderService,
    val collectExecutorService: CollectExecutorService
) : UserInfoService {

    override fun getUserInfo(userInfoRequest: UserInfoRequest): UserInfoResponse? {

        return kotlin.runCatching {
            let {
                validationService.validateOrThrows(userInfoRequest)
            }
            .let { userInfoRequest ->
                HeaderInfo().apply {
                    this.banksaladUserId = userInfoRequest.banksaladUserId
                    this.organizationObjectId = userInfoRequest.organizationObjectId
                }
            }
            .let { headerInfo ->
                headerService.getHeader(headerInfo)
            }
            .let { header ->
                val res: ApiResponse<UserInfoResponse> = collectExecutorService.execute(
                    Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.userInfo),
                    ApiRequest.builder<UserInfoRequest>()
                        .headers(header)
                        .request(userInfoRequest)
                        .build()
                )
                res
            }
            .takeIf { res ->
                res.httpStatusCode == 200
            }
            ?.response
            ?: kotlin.run {
                throw Exception("GetUserInfo ApiResponse httpStatusCode Not 200")
            }
        }
        .getOrThrow()
    }
}
