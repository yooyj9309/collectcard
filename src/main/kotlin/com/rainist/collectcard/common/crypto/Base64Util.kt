package com.rainist.collectcard.common.crypto

import java.util.Base64

object Base64Util {
    fun encode(src: ByteArray?): String? {
        return Base64.getEncoder().withoutPadding().encodeToString(src)
    }

    fun decode(src: String?): ByteArray? {
        return Base64.getDecoder().decode(src)
    }
}
