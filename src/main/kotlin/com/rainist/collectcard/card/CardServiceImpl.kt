package com.rainist.collectcard.card

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.card.dto.ListCardsRequest
import com.rainist.collectcard.card.dto.ListCardsRequestDataBody
import com.rainist.collectcard.card.dto.ListCardsResponse
import com.rainist.collectcard.card.dto.toListCardsResponseProto
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.header.HeaderService
import com.rainist.collectcard.header.dto.HeaderInfo
import com.rainist.common.log.Log
import org.springframework.stereotype.Service

@Service
class CardServiceImpl(
    val collectExecutorService: CollectExecutorService,
    val headerService: HeaderService
) : CardService {

    companion object : Log

    override fun listCards(header: MutableMap<String, String?>, listCardsRequest: ListCardsRequest): ListCardsResponse {
        return runCatching<ExecutionResponse<ListCardsResponse>> {
            collectExecutorService.execute(
                Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.cards),
                ExecutionRequest.builder<ListCardsRequest>()
                    .headers(header)
                    .request(listCardsRequest)
                    .build()
            )
        }.onFailure {
            logger.error("Failed to retrieve card list: ${it.message}")
            throw CardsException("exception", it)
        }.getOrThrow().response
    }

    fun listCards(request: CollectcardProto.ListCardsRequest): CollectcardProto.ListCardsResponse {
        return kotlin.runCatching {
            ListCardsRequest().apply {
                this.dataBody = ListCardsRequestDataBody()
            }.let { listCardsRequest ->

                HeaderInfo().apply {
                    this.banksaladUserId = request.userId
                    this.organizationObjectid = request.companyId.value
                }.let { headerInfo ->
                    headerService.getHeader(headerInfo)
                }.let { header ->
                    listCards(header, listCardsRequest)
                }
            }.toListCardsResponseProto()
        }.getOrThrow()
    }
}
