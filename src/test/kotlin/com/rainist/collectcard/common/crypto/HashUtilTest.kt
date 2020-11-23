package com.rainist.collectcard.common.crypto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class HashUtilTest {

    @Test
    fun sha256_success() {
        var src = "Hello"
        var sha256 = HashUtil.sha256(src)

        println(sha256)
        assertEquals("GF+NsyJx/iX1Yab8k4suJkMG7DBO2lGAB9F2SCY4GWk", sha256)

        src = "World"
        sha256 = HashUtil.sha256(src)

        println(sha256)
        assertEquals("eK5kfcVUTSJxMKBoKlHjC8d3f7ttio8XAHRjo+zR1SQ", sha256)
    }
}
