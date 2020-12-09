package com.rainist.collectcard.common.util

import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest()
class CustomStringUtilTest {

    @Test
    fun replaceNumberToMaskTest() {
        var cardNumber1 = "9523*********8721"
        var cardNumber1_new = "952333*******8721"
        assertEquals(cardNumber1, CustomStringUtil.replaceNumberToMask(cardNumber1_new))

        var cardNumber2 = "9523*********872"
        var cardNumber2_new = "952333*******872"
        assertEquals(cardNumber2, CustomStringUtil.replaceNumberToMask(cardNumber2_new))

        var cardNumber3 = null
        assertEquals("", CustomStringUtil.replaceNumberToMask(cardNumber3))
    }
}
