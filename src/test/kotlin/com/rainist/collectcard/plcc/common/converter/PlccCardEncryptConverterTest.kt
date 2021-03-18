package com.rainist.collectcard.plcc.common.converter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PlccCardEncryptConverterTest {

    @Autowired
    lateinit var plccCardEncryptConverter: PlccCardEncryptConverter

    @Autowired
    lateinit var plccCardTransactionEncryptConverter: PlccCardTransactionEncryptConverter

    @Test
    @DisplayName("plccCardEncryptConverter 암복호화 테스트")
    fun plccCardEncryptConverter_test() {
        // given
        val encryptedField = "apvEOus41KOcI6xr7MxcVGo6WiP3G6MNOYRHJjzSKO4"
        val decryptedField = "1231234564561234"

        // when
        val convertToDatabaseColumn = plccCardEncryptConverter.convertToDatabaseColumn(decryptedField)
        val convertToEntityAttribute = plccCardEncryptConverter.convertToEntityAttribute(encryptedField)

        // then
        assertAll(
            "plcc_card_encrypt",
            { assertThat(convertToDatabaseColumn).isEqualTo(encryptedField) },
            { assertThat(convertToEntityAttribute).isEqualTo(decryptedField) }
        )
    }

    @Test
    @DisplayName("plccCardTransactionEncryptConverter 암복호화 테스트")
    fun plccCardTransactionEncryptConverter_test() {
        // given
        val encryptedField = "bIxYIKnNjW1pqtpIlapOmfVQ+KDEsU+uQDq/rBH5DN8"
        val decryptedField = "1231234564561234"

        // when
        val convertToDatabaseColumn = plccCardTransactionEncryptConverter.convertToDatabaseColumn(decryptedField)
        val convertToEntityAttribute = plccCardTransactionEncryptConverter.convertToEntityAttribute(encryptedField)

        // then
        assertAll(
            "plcc_card_trnasaction_encrypt",
            { assertThat(convertToDatabaseColumn).isEqualTo(encryptedField) },
            { assertThat(convertToEntityAttribute).isEqualTo(decryptedField) }
        )
    }
}
