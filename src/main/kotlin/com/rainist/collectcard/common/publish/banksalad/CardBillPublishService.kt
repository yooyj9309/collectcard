package com.rainist.collectcard.common.publish.banksalad

import com.rainist.collectcard.cardbills.dto.ListCardBillsResponse
import com.rainist.collectcard.common.dto.CollectShadowingResponse
import java.time.LocalDateTime
import org.springframework.stereotype.Service

@Service
class CardBillPublishService {
    fun sync(banksaladUserId: Long, organizationId: String, lastCheckAt: LocalDateTime) {
        TODO()
    }

    fun shadowing(banksaladUserId: Long, organizationId: String, lastCheckAt: LocalDateTime?, executionRequestId: String, oldResponse: ListCardBillsResponse): CollectShadowingResponse {
        TODO()
    }
}
