package com.rainist.collectcard.common.dto

import com.rainist.common.util.DateTimeUtil
import java.time.LocalDateTime

class NowUtcLocalDatetime(
    val now: LocalDateTime = DateTimeUtil.utcNowLocalDateTime()
)
