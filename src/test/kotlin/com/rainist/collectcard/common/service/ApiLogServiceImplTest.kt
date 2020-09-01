package com.rainist.collectcard.common.service

import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest()
internal class ApiLogServiceImplTest {

    @Autowired
    lateinit var apiLogService: ApiLogServiceImpl

    @Test
    fun parseResultCodeAndMessage_ResultExist() {
        val json =
            """
                {
                    "dataHeader": {
                        "successCode": "0",
                        "resultCode": "0010",
                        "reqKey": null,
                        "resultMessage": "다음 조회건이 있습니다."
                    },
                    "dataBody": {}
                }
            """.trimIndent()

        val result = apiLogService.parseResultCodeAndMessage(json)

        Assert.assertEquals(result.first, "0010")
        Assert.assertEquals(result.second, "다음 조회건이 있습니다.")
    }

    @Test
    fun parseResultCodeAndMessage_ResultNotExist() {
        val json =
            """
                {
                    "dataHeader": {
                        "successCode": "0",
                        "resultCode123": "0010",
                        "reqKey": null,
                        "resultMessage123": "다음 조회건이 있습니다."
                    },
                    "dataBody": {}
                }
            """.trimIndent()

        val result = apiLogService.parseResultCodeAndMessage(json)

        Assert.assertEquals(result.first, "")
        Assert.assertEquals(result.second, "")
    }

    @Test
    fun parseResultCodeAndMessage_JsonBlank() {
        val json = "     "

        val result = apiLogService.parseResultCodeAndMessage(json)

        Assert.assertEquals(result.first, "")
        Assert.assertEquals(result.second, "")
    }
}
