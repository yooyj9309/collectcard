package com.rainist.collectcard.common.util

import com.rainist.collect.common.api.Api
import com.rainist.collect.common.execution.Execution
import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.common.util.DateTimeUtil
import java.util.UUID
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.util.ResourceUtils

class ExecutionTestUtil {
    companion object {
        fun serverSetting(server: MockRestServiceServer, api: Api, vararg filePathList: String) {
            filePathList.forEach {
                server.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(api.endpoint))
                    .andExpect(MockRestRequestMatchers.method(HttpMethod.valueOf(api.method.name)))
                    .andRespond(
                        MockRestResponseCreators.withStatus(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(readText(it))
                    )
            }
        }

        fun <T> getExecutionResponse(collectExecutorService: CollectExecutorService, execution: Execution, executionContext: ExecutionContext, executionRequest: ExecutionRequest<*>): ExecutionResponse<T> {
            return collectExecutorService.execute(
                executionContext,
                execution,
                executionRequest
            )
        }

        fun getExecutionContext(banksaladUserId: String, organizationId: String): ExecutionContext {
            return CollectExecutionContext(
                executionRequestId = UUID.randomUUID().toString(),
                organizationId = organizationId,
                userId = banksaladUserId,
                startAt = DateTimeUtil.utcNowLocalDateTime()
            )
        }

        private fun readText(fileInClassPath: String): String {
            return ResourceUtils.getFile(fileInClassPath).readText(Charsets.UTF_8)
        }
    }
}
