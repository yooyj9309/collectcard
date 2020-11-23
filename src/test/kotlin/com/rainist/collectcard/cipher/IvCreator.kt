package com.rainist.collectcard.cipher

import com.rainist.collectcard.common.crypto.Base64Util
import java.security.SecureRandom
import java.util.stream.IntStream
import org.junit.jupiter.api.Test

class IvCreator {

    @Test
    fun generateRandomIv(): ByteArray {
        val iv = ByteArray(12)
        val random = SecureRandom()
        random.nextBytes(iv)
        return iv
    }
    @Test
    fun printGenerateRandomIv() {

        IntStream.range(0, 12)
            .forEach { value -> println(Base64Util.encode(generateRandomIv())) }
    }
}
