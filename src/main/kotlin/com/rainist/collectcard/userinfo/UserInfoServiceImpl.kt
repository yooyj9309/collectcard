package com.rainist.collectcard.userinfo

import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.service.OrganizationService
import com.rainist.collectcard.userinfo.dto.UserInfoRequest
import com.rainist.collectcard.userinfo.dto.UserInfoResponse
import com.rainist.common.service.ValidationService
import org.springframework.stereotype.Service

@Service
class UserInfoServiceImpl(
    private val headerService: HeaderService,
    private val organizationService: OrganizationService,
    private val validationService: ValidationService,
    private val collectExecutorService: CollectExecutorService
) : UserInfoService {

    override fun getUserInfo(userInfoRequest: UserInfoRequest): UserInfoResponse? {
        TODO("not used")
//        return kotlin.runCatching {
//            let {
//                validationService.validateOrThrows(userInfoRequest)
//            }
//                .let { userInfoRequest ->
//                    HeaderInfo().apply {
//                        this.banksaladUserId = userInfoRequest.banksaladUserId
//                        this.organizationObjectid = userInfoRequest.organizationObjectid
//                        this.clientId = organizationService.getOrganizationByObjectId(
//                            userInfoRequest.organizationObjectid ?: ""
//                        )?.clientId
//                    }
//                }
//                .let { headerInfo ->
//                    headerService.getHeader(headerInfo)
//                }
//                .let { header ->
//                    // TODO : add api logging
//                    val res: ExecutionResponse<UserInfoResponse> = collectExecutorService.execute(
//                        Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.userInfo),
//                        ExecutionRequest.builder<UserInfoRequest>()
//                            .headers(header)
//                            .request(userInfoRequest)
//                            .build(), null, null
//                    )
//                    res
//                }
//                .takeIf { res ->
//                    res.httpStatusCode == 200
//                }
//                ?.response
//                ?: kotlin.run {
//                    throw Exception("GetUserInfo ExecutionResponse httpStatusCode Not 200")
//                }
//        }
//            .getOrThrow()
    }
}
