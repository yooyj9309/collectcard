package com.rainist.collectcard.cardbills

import com.rainist.collect.common.dto.ApiRequest
import com.rainist.collect.common.dto.ApiResponse
import com.rainist.collect.executor.service.CollectExecutorService
import com.rainist.collectcard.cardbills.dto.ListCardBillsRequest
import com.rainist.collectcard.cardbills.dto.ListCardBillsResponse
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.common.exception.CollectcardException
import com.rainist.common.log.Log
import org.springframework.stereotype.Service

@Service
class CardBillServiceImpl(val collectExecutorService: CollectExecutorService) : CardBillService {

    companion object : Log

    // TODO : implement proper header fields
    private fun makeHeader(): MutableMap<String, String> {
        return mutableMapOf<String, String>(
            "Authorization" to "bearer",
            "userDeviceId" to "4b7626ac-a43a-4dd3-905f-66e31aa5c2b3",
            "deviceOs" to "Android"
        )
    }

    override fun listUserCardBills(listCardBillsRequest: ListCardBillsRequest): ListCardBillsResponse {
        return this.listCardBills(listCardBillsRequest)
    }

    fun listUserCardBillsExpected(listCardBillsRequest: ListCardBillsRequest): ListCardBillsResponse {

        return runCatching<ApiResponse<ListCardBillsResponse>> {
            collectExecutorService.execute(
                Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.cardBillsExpected),
                ApiRequest.builder<ListCardBillsRequest>()
                    .headers(makeHeader())
                    .request(listCardBillsRequest)
                    .build()
            )
        }.onFailure {
            logger.error("Failed to retrieve bills expected list: ${it.message}")
            throw CollectcardException("exception", it)
        }.getOrThrow().response
    }

    fun listCardBills(listCardBillsRequest: ListCardBillsRequest): ListCardBillsResponse {

        return runCatching<ApiResponse<ListCardBillsResponse>> {
            collectExecutorService.execute(
                Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.cardbills),
                ApiRequest.builder<ListCardBillsRequest>()
                    .headers(makeHeader())
                    .request(listCardBillsRequest)
                    .build()
            )
        }.onFailure {
            logger.error("Failed to retrieve bills list: ${it.message}")
            throw CollectcardException("exception", it)
        }.getOrThrow().response
    }
}
