package com.rainist.collectcard.plcc.cardrewards

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.util.FileUtil
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsRequest
import com.rainist.collectcard.plcc.cardrewards.dto.PlccCardRewardsRequestDataBody
import com.rainist.collectcard.plcc.cardrewards.dto.PlccRpcRequest
import org.apache.http.entity.ContentType
import org.springframework.http.HttpStatus
import java.util.UUID

class RewardsApiMockSetting {

    companion object {

        private const val URL_REGEX = "/banksalad/service-limit-info"
        private const val THRESHOLD_REQUEST_BODY_JSON =
            "classpath:mock/lottecard/rewards/request/lotte_02_1_threshold.json"
        private const val THRESHOLD_RESPONSE_BODY_JSON =
            "classpath:mock/lottecard/rewards/response/rewards_threshold_expected_1.json"
        private const val REWARDS_REQUEST_BODY_JSON =
            "classpath:mock/lottecard/rewards/request/lotte_02_1_rewards.json"
        private const val REWARDS_RESPONSE_BODY_JSON =
            "classpath:mock/lottecard/rewards/response/rewards_typeLimit_expected_1.json"

        fun setupMockServer(
            wireMockServer: WireMockServer,
            api: RewardsApis
        ) {
            when (api) {
                RewardsApis.THRESHOLD -> wireMockServer.stubFor(
                    WireMock.post(WireMock.urlMatching(URL_REGEX))
                        .withRequestBody(WireMock.equalToJson(FileUtil.readText(THRESHOLD_REQUEST_BODY_JSON)))
                        .willReturn(
                            WireMock.aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                                .withBody(FileUtil.readText(THRESHOLD_RESPONSE_BODY_JSON))
                        )
                )

                RewardsApis.REWARDS -> wireMockServer.stubFor(
                    WireMock.post(WireMock.urlMatching(URL_REGEX))
                        .withRequestBody(WireMock.equalToJson(FileUtil.readText(REWARDS_REQUEST_BODY_JSON)))
                        .willReturn(
                            WireMock.aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                                .withBody(FileUtil.readText(REWARDS_RESPONSE_BODY_JSON))
                        )
                )
            }
        }

        fun makeRewardsRequest(inquiryYearMonth: String): ExecutionRequest<PlccCardRewardsRequest> {
            return ExecutionRequest.builder<PlccCardRewardsRequest>()
                .headers(mutableMapOf<String, String>())
                .request(PlccCardRewardsRequest().apply {
                    dataBody = PlccCardRewardsRequestDataBody(inquiryYearMonth, "376277600833685")
                })
                .build()
        }

        fun makePlccRpcRequestOnMar(): PlccRpcRequest {
            return PlccRpcRequest(
                cardId = "376277600833685",
                requestMonthMs = 1615427107000 // 2021년 3월 11일 목요일 오전 10:45:07 GMT+09:00
            )
        }

        fun makePlccRpcRequestOnFeb(): PlccRpcRequest {
            return PlccRpcRequest(
                cardId = "376277600833685",
                requestMonthMs = 1613026702000 // 2021년 2월 11일 목요일 오후 3:58:22 GMT+09:00
            )
        }

        fun makeCollectExecutionContext() = CollectExecutionContext(
            executionRequestId = UUID.randomUUID().toString(),
            userId = "1",
            organizationId = "596d66692c4069c168b57c77"
        )
    }

    enum class RewardsApis {
        THRESHOLD, REWARDS
    }
}
