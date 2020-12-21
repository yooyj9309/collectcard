package com.rainist.collectcard.common.publish.banksalad

import java.time.LocalDateTime
import org.springframework.stereotype.Service

@Service
class PublishService {
    fun syncBanksaladServices(banksaladUserId: Long, organizationId: String, lastCheckAt: LocalDateTime) {
        TODO("각 publish service의 sync 호출")
    }
}
