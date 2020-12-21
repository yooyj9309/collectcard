package com.rainist.collectcard.common.publish.banksalad

import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse
import com.rainist.collectcard.common.dto.CollectShadowingResponse
import java.time.LocalDateTime
import org.springframework.stereotype.Service

@Service
class CardTransactionPublishService {
    fun sync(banksaladUserId: Long, organizationId: String, lastCheckAt: LocalDateTime) {
        TODO()
    }

    fun shadowing(banksaladUserId: Long, organizationId: String, lastCheckAt: LocalDateTime?, executionRequestId: String, oldResponse: ListTransactionsResponse): CollectShadowingResponse {
        TODO()
    }
}
