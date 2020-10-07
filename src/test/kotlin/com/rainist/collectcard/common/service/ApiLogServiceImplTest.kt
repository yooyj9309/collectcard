package com.rainist.collectcard.common.service

import com.rainist.collect.executor.ApiLog
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.db.entity.ApiLogEntity
import com.rainist.collectcard.common.db.repository.ApiLogRepository
import java.time.LocalDateTime
import java.time.ZoneId
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

@ExtendWith(SpringExtension::class)
@SpringBootTest()
class ApiLogServiceImplTest {

    @Autowired
    lateinit var apiLogService: ApiLogServiceImpl

    @Autowired
    lateinit var apiLogRepository: ApiLogRepository

    val organizationId = "shinhancard"
    val banksaladUserId = 1L

    @Test
    @Rollback
    @Transactional
    fun logRequestTest_emptyRequest() {
        // Request는 생성되어있으나 안에 데이터가 없는경우.
        var executionRequestId = "f4541ba6-2fd4-4291-8c88-6a038d0e57f8"
        var apiLog = ApiLog.builder()
            .id("id1")
            .request(ApiLog.Request.builder().build())
            .api(ShinhancardApis.card_shinhancard_cards)
            .build()
        // 비어있는 apiLog 주입
        apiLogService.logRequest(executionRequestId, organizationId, banksaladUserId, apiLog)

        var apiLogEntities = findApiLogEntities(executionRequestId)

        assertEquals(1, apiLogEntities.size)
        assertEquals("", apiLogEntities[0].requestHeaderText)
        assertEquals("", apiLogEntities[0].requestBodyText)
        assertEquals("", apiLogEntities[0].transformedRequestHeaderText)
        assertEquals("", apiLogEntities[0].transformedRequestBodyText)

        // Requsst 데이터가 없는경우.
        executionRequestId = "ad359761-db06-4d0e-bde6-eb70aec6ae97"
        apiLog = ApiLog.builder()
            .id("id2")
            .api(ShinhancardApis.card_shinhancard_cards)
            .build()
        // 비어있는 apiLog 주입
        apiLogService.logRequest(executionRequestId, organizationId, banksaladUserId, apiLog)
        apiLogEntities = findApiLogEntities(executionRequestId)

        assertEquals(1, apiLogEntities.size)
        assertEquals("", apiLogEntities[0].requestHeaderText)
        assertEquals("", apiLogEntities[0].requestBodyText)
        assertEquals("", apiLogEntities[0].transformedRequestHeaderText)
        assertEquals("", apiLogEntities[0].transformedRequestBodyText)
    }

    @Test
    @Rollback
    @Transactional
    fun logRequestTest_requestWithData() {
        val executionRequestId = "ad359761-db06-4d0e-bde6-eb70aec6ae97"
        val apiLog = ApiLog.builder()
            .id("id2")
            .request(ApiLog.Request.builder()
                .header("header")
                .body("body")
                .transformedHeader("transformedHeader")
                .transformedBody("transformedBody")
                .build()
            )
            .api(ShinhancardApis.card_shinhancard_cards)
            .build()

        // 비어있는 apiLog 주입
        apiLogService.logRequest(executionRequestId, organizationId, banksaladUserId, apiLog)
        val apiLogEntities = findApiLogEntities(executionRequestId)

        assertEquals(1, apiLogEntities.size)
        assertEquals("header", apiLogEntities[0].requestHeaderText)
        assertEquals("body", apiLogEntities[0].requestBodyText)
        assertEquals("transformedHeader", apiLogEntities[0].transformedRequestHeaderText)
        assertEquals("transformedBody", apiLogEntities[0].transformedRequestBodyText)
    }

    @Test
    @Rollback
    @Transactional
    fun logResponseTest_emptyRequest() {
        var executionRequestId = "6d228a8e-2792-49e5-a578-5cebe6ea9fee"
        var apiLog = ApiLog.builder()
            .id("id1")
            .api(ShinhancardApis.card_shinhancard_cards)
            .build()

        apiLogService.logResponse(executionRequestId, organizationId, banksaladUserId, apiLog)

        var apiLogEntities = findApiLogEntities(executionRequestId)
        assertEquals(1, apiLogEntities.size)
        assertEquals("", apiLogEntities[0].requestHeaderText)
        assertEquals("", apiLogEntities[0].requestBodyText)
        assertEquals("", apiLogEntities[0].transformedRequestHeaderText)
        assertEquals("", apiLogEntities[0].transformedRequestBodyText)
        assertEquals(null, apiLogEntities[0].responseBodyText)
        assertEquals(null, apiLogEntities[0].responseHeaderText)
        assertEquals(null, apiLogEntities[0].transformedResponseBodyText)
        assertEquals(null, apiLogEntities[0].transformedResponseHeaderText)
    }

    @Test
    @Rollback
    @Transactional
    fun logResponseTest_responseWithData() {
        val executionRequestId = "ad359761-db06-4d0e-bde6-eb70aec6ae97"
        val apiLog = ApiLog.builder()
            .id("id2")
            .response(ApiLog.Response.builder()
                .responseCode("200")
                .body("body")
                .header("header")
                .transformedBody("transformedBody")
                .transformedHeader("transformedHeader")
                .build()
            )
            .api(ShinhancardApis.card_shinhancard_cards)
            .build()

        apiLogService.logResponse(executionRequestId, organizationId, banksaladUserId, apiLog)
        val apiLogEntities = findApiLogEntities(executionRequestId)
        assertEquals(1, apiLogEntities.size)
        assertEquals("", apiLogEntities[0].requestHeaderText)
        assertEquals("", apiLogEntities[0].requestBodyText)
        assertEquals("", apiLogEntities[0].transformedRequestHeaderText)
        assertEquals("", apiLogEntities[0].transformedRequestBodyText)
        assertEquals("body", apiLogEntities[0].responseBodyText)
        assertEquals("header", apiLogEntities[0].responseHeaderText)
        assertEquals("transformedBody", apiLogEntities[0].transformedResponseBodyText)
        assertEquals("transformedHeader", apiLogEntities[0].transformedResponseHeaderText)
    }

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

    fun findApiLogEntities(executionRequestId: String): List<ApiLogEntity> {
        return apiLogRepository.findByExecutionRequestIdAndCreatedAtBetween(
            executionRequestId,
            LocalDateTime.now(ZoneId.of("UTC")).minusDays(1),
            LocalDateTime.now(ZoneId.of("UTC")).plusDays(1)
        )
    }
}
