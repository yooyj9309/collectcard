package com.rainist.collectcard.common.util

import com.rainist.common.util.DateTimeUtil
import java.time.LocalDateTime
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class DatetimeUtilTest {

    @Test
    @DisplayName("utcLocalDateTimeToEpochMilliSecond 테스트")
    fun utcLocalDateTimeToEpochMilliSecond_test() {
        // given
        val localDate = LocalDateTime.now() // 202103311013...

        // when
        val utcLocalDateTimeToEpochMilliSecond = DateTimeUtil.kstLocalDateTimeToEpochMilliSecond(localDate)

        println("utcLocalDateTimeToEpochMilliSecond = $utcLocalDateTimeToEpochMilliSecond")
    }
}
