package com.rainist.collectcard.cardtransactions

import com.rainist.collect.executor.service.CollectExecutorService
import com.rainist.collectcard.card.dto.ListCardsRequest
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse
import com.rainist.common.log.Log
import org.springframework.stereotype.Service

@Service
class CardTransactionServiceImpl(
    val collectExecutorService: CollectExecutorService
) : CardTransactionService {

    companion object : Log

    override fun listTransactions(listCardsRequest: ListCardsRequest): ListTransactionsResponse {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}
