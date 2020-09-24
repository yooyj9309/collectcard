package com.rainist.collectcard.common.service

import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collectcard.card.dto.Card
import com.rainist.collectcard.common.db.entity.ApiLogEntity
import com.rainist.collectcard.common.db.repository.ApiLogRepository
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.enums.ResultCode
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@DisplayName("ApiLog validate Test")
class ExecutionResponseValidateServiceTest {

    @Autowired
    lateinit var executionResponseValidateService: ExecutionResponseValidateService

    @Autowired
    lateinit var apiLogRepository: ApiLogRepository

    val requestId: String = "FWGQCSCVRB"

    @Test
    fun executionValidTest_success() {
        val executionContext = makeExecutionContext()
        val executionResponse: ExecutionResponse<Card> = makeExecutionResponse(200, false)

        val validationResponse = executionResponseValidateService.validate(executionContext, executionResponse)
        assertEquals(true, validationResponse)
    }

    @Test
    fun executionValidTest_fail_exceptionOccurredIsTrue() {
        val executionContext = makeExecutionContext()
        val executionResponse: ExecutionResponse<Card> = makeExecutionResponse(200, true)

        val validationResponse = executionResponseValidateService.validate(executionContext, executionResponse)
        assertEquals(false, validationResponse)
    }

    @Test
    @Rollback
    @Transactional
    fun executionValidTest_fail_EXTERNAL_SERVER_ERROR() {
        val executionContext = makeExecutionContext()
        val executionResponse: ExecutionResponse<Card> = makeExecutionResponse(200, false)
        saveApiLog(ResultCode.OK.name)
        saveApiLog(ResultCode.EXTERNAL_SERVER_ERROR.name)

        val validationResponse = executionResponseValidateService.validate(executionContext, executionResponse)
        assertEquals(false, validationResponse)
    }

    @Test
    @Rollback
    @Transactional
    fun executionValidTest_fail_INVALID_ACCESS_TOKEN() {
        val executionContext = makeExecutionContext()
        val executionResponse: ExecutionResponse<Card> = makeExecutionResponse(200, false)
        saveApiLog(ResultCode.OK.name)
        saveApiLog(ResultCode.INVALID_ACCESS_TOKEN.name)

        val validationResponse = executionResponseValidateService.validate(executionContext, executionResponse)
        assertEquals(false, validationResponse)
    }

    @Test
    @Rollback
    @Transactional
    fun executionValidTest_INVALID_USER() {
        val executionContext = makeExecutionContext()
        val executionResponse: ExecutionResponse<Card> = makeExecutionResponse(200, false)
        saveApiLog(ResultCode.OK.name)
        saveApiLog(ResultCode.INVALID_USER.name)

        val validationResponse = executionResponseValidateService.validate(executionContext, executionResponse)
        assertEquals(false, validationResponse)
    }

    @Test
    @Rollback
    @Transactional
    fun executionValidTest_UNKNOWN() {
        val executionContext = makeExecutionContext()
        val executionResponse: ExecutionResponse<Card> = makeExecutionResponse(200, false)
        saveApiLog(ResultCode.OK.name)
        saveApiLog(ResultCode.UNKNOWN.name)

        val validationResponse = executionResponseValidateService.validate(executionContext, executionResponse)
        assertEquals(false, validationResponse)
    }

    private fun makeExecutionContext(): ExecutionContext {
        return CollectExecutionContext(requestId, "1", "shinhancard")
    }

    private fun makeExecutionResponse(httpStatusCode: Int, exceptionOccurred: Boolean): ExecutionResponse<Card> {
        return ExecutionResponse.builder<Card>()
            .httpStatusCode(httpStatusCode)
            .exceptionOccurred(exceptionOccurred)
            .headers(mapOf<String, String>())
            .response(Card())
            .build()
    }

    fun saveApiLog(resultCode: String) {
        apiLogRepository.save(ApiLogEntity().apply {
            this.executionRequestId = requestId
            this.apiRequestId = ""
            this.organizationId = ""
            this.banksaladUserId = 1
            this.apiId = ""
            this.organizationApiId = ""
            this.requestUrl = ""
            this.httpMethod = ""
            this.httpMethod = ""
            this.requestBodyText = ""
            this.requestHeaderText = ""
            this.transformedRequestHeaderText = ""
            this.transformedRequestBodyText = ""
            this.responseHeaderText = ""
            this.responseBodyText = ""
            this.transformedResponseHeaderText = ""
            this.transformedResponseBodyText = ""
            this.requestDatetime = LocalDateTime.now()
            this.resultCode = resultCode
        })
    }
}
