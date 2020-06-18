package com.rainist.collectcard.cardtransactions

import com.rainist.collectcard.card.dto.ListCardsRequest
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse

interface CardTransactionService {
    fun listTransactions(listCardsRequest: ListCardsRequest): ListTransactionsResponse
}
