package com.rainist.collectcard.cardtransactions

import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse
import com.rainist.collectcard.common.service.CardOrganization

interface CardTransactionService {
    fun listTransactions(banksaladUserId: String, organization: CardOrganization, fromMs: Long?): ListTransactionsResponse
}
