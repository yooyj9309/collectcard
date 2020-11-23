package com.rainist.collectcard.common.crypto

object HashUtil {
    fun sha256(src: String): String? {
        val sh256Bytes = org.apache.commons.codec.digest.DigestUtils.sha256(src)
        return Base64Util.encode(sh256Bytes)
    }
}
