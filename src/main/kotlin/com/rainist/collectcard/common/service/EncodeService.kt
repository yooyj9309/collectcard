package com.rainist.collectcard.common.service

import java.nio.charset.Charset
import java.util.Base64
import org.springframework.stereotype.Service

@Service
class EncodeService {

    fun base64Encode(value: String, charset: Charset = Charsets.UTF_8): String {
        return Base64.getEncoder().encodeToString(value.toByteArray(charset))
    }

    fun base64Decode(value: String, charset: Charset = Charsets.UTF_8): String {
        val bytes = Base64.getDecoder().decode(value)
        return String(bytes, charset)
    }

    fun base64UrlEncode(value: String, charset: Charset = Charsets.UTF_8): String {
        return Base64.getUrlEncoder().encodeToString(value.toByteArray(charset))
    }

    fun base64UrlDecode(value: String, charset: Charset = Charsets.UTF_8): String {
        val bytes = Base64.getUrlDecoder().decode(value)
        return String(bytes, charset)
    }
}
