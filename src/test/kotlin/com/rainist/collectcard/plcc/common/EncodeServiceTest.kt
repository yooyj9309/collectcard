package com.rainist.collectcard.plcc.common

import com.rainist.collectcard.common.service.EncodeService
import java.nio.charset.Charset
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class EncodeServiceTest {

    @Autowired
    lateinit var encodeService: EncodeService

    @DisplayName("base64 decoding 테스트")
    @Test
    fun base64_decode_test() {
        // given
        val rewardsDecodedMessage =
            "W8f9xcO6sL3HwPvH0bW1wbbIuF3A1LfCx8+9xSDEq7XlufjIoyAowMwpsKEgwK/Iv8fPwfYgvsq9wLTPtNkuIMSrteW5+MijIMiuwM652bb4tM+02S4="
        val transactionsDecodedMessage =
            "W8f9xcO6sMD7v+uzu7+qwbbIuF3A1LfCx8+9xSDEq7XlufjIoyAowMwpsKEgwK/Iv8fPwfYgvsq9wLTPtNkuIMSrteW5+MijIMiuwM652bb4tM+02S4="

        // when
        val decodedMessage1 = encodeService.base64Decode(rewardsDecodedMessage, Charset.forName("MS949"))
        val decodedMessage2 = encodeService.base64Decode(transactionsDecodedMessage, Charset.forName("MS949"))

        // then
        assertThat(decodedMessage1).isEqualTo("[혜택별실적한도조회]입력하신 카드번호 (이)가 유효하지 않습니다. 카드번호 확인바랍니다.")
        assertThat(decodedMessage2).isEqualTo("[혜택별적용내역조회]입력하신 카드번호 (이)가 유효하지 않습니다. 카드번호 확인바랍니다.")
    }
}
