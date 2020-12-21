package com.rainist.collectcard.common.publish.banksalad

import com.rainist.collectcard.cardloans.dto.ListLoansResponse
import com.rainist.collectcard.common.dto.CollectShadowingResponse
import java.time.LocalDateTime
import org.springframework.stereotype.Service

@Service
class CardLoanPublishService {
    fun sync(banksaladUserId: Long, organizationId: String, lastCheckAt: LocalDateTime) {
        TODO()
    }

    fun shadowing(banksaladUserId: Long, organizationId: String, lastCheckAt: LocalDateTime?, executionRequestId: String, oldResponse: ListLoansResponse): CollectShadowingResponse {
        TODO()
    }
}
