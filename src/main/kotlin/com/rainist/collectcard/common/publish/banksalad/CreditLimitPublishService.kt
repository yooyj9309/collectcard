package com.rainist.collectcard.common.publish.banksalad

import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitResponse
import com.rainist.collectcard.common.dto.CollectShadowingResponse
import java.time.LocalDateTime
import org.springframework.stereotype.Service

@Service
class CreditLimitPublishService {
    fun sync(banksaladUserId: Long, organizationId: String, lastCheckAt: LocalDateTime) {
        TODO()
    }

    fun shadowing(banksaladUserId: Long, organizationId: String, lastCheckAt: LocalDateTime?, executionRequestId: String, oldResponse: CreditLimitResponse): CollectShadowingResponse {
        TODO()
    }
}
