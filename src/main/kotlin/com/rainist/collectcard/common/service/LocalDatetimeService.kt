package com.rainist.collectcard.common.service

import com.rainist.collectcard.common.dto.NowUtcLocalDatetime
import org.springframework.stereotype.Service

@Service
class LocalDatetimeService {
    fun generateNowLocalDatetime(): NowUtcLocalDatetime {
        return NowUtcLocalDatetime()
    }
}
