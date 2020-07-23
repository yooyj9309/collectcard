package com.rainist.collectcard.userinfo

import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.service.OrganizationService
import com.rainist.collectcard.header.dto.HeaderInfo
import com.rainist.collectcard.userinfo.dto.UserInfoRequest
import com.rainist.collectcard.userinfo.dto.UserInfoResponse
import com.rainist.common.service.ValidationService
import org.springframework.stereotype.Service

@Service
class UserInfoServiceImpl(
    val headerService: HeaderService,
    val organizationService: OrganizationService,
    val validationService: ValidationService,
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
                        this.organizationObjectid = userInfoRequest.organizationObjectid
                        this.clientId = organizationService.getOrganizationByObjectId(
                            userInfoRequest.organizationObjectid ?: ""
                        )?.clientId
                    }
                }
                .let { headerInfo ->
                    headerService.getHeader(headerInfo)
                }
                .let { header ->
                    // TODO : add api logging
                    val res: ExecutionResponse<UserInfoResponse> = collectExecutorService.execute(
                        Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.userInfo),
                        ExecutionRequest.builder<UserInfoRequest>()
                            .headers(header)
                            .request(userInfoRequest)
                            .build(), null, null
                    )
                    res
                }
                .takeIf { res ->
                    res.httpStatusCode == 200
                }
                ?.response
                ?: kotlin.run {
                    throw Exception("GetUserInfo ExecutionResponse httpStatusCode Not 200")
                }
        }
            .getOrThrow()
    }
}
