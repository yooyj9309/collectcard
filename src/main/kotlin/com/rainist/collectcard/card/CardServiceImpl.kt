package com.rainist.collectcard.card

import com.rainist.collect.common.dto.ApiRequest
import com.rainist.collect.common.dto.ApiResponse
import com.rainist.collect.executor.service.CollectExecutorService
import com.rainist.collectcard.card.dto.ListCardsRequest
import com.rainist.collectcard.card.dto.ListCardsResponse
import com.rainist.collectcard.common.collect.BusinessType
import com.rainist.collectcard.common.collect.Executions
import com.rainist.collectcard.common.collect.Organization
import com.rainist.collectcard.common.collect.Transaction
import com.rainist.common.log.Log
import org.springframework.stereotype.Service

@Service
class CardServiceImpl(val collectExecutorService: CollectExecutorService) : CardService {

    companion object : Log

    // TODO : implement proper header fields
    private fun makeHeader(): MutableMap<String, String> {
        return mutableMapOf<String, String>(
            "Authorization" to "bearer",
            "userDeviceId" to "4b7626ac-a43a-4dd3-905f-66e31aa5c2b3",
            "deviceOs" to "Android"
        )
    }

    override fun listCards(listCardsRequest: ListCardsRequest): ListCardsResponse {
        return runCatching<ApiResponse<ListCardsResponse>> {
            collectExecutorService.execute(
                Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.cards),
                ApiRequest.builder<ListCardsRequest>()
                    .headers(makeHeader())
                    .request(listCardsRequest)
                    .build()
            )
        }.onFailure {
            logger.error("Failed to retrieve card list: ${it.message}")
            throw CardsException("exception", it)
        }.getOrThrow().response
    }
}
