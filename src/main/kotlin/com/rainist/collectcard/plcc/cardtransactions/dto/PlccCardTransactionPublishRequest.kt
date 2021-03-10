package com.rainist.collectcard.plcc.cardtransactions.dto

data class PlccCardTransactionPublishRequest(
    var cardId: String? = null,
    var requestMonthMs: Long? = null
)
