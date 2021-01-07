package com.rainist.collectcard.grpc.client

import com.github.banksalad.idl.apis.v1.card.CardGrpc
import com.rainist.common.log.Log
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("production", "development", "staging", "local")
class CardClientService(
    @Qualifier("cardBlockingStub")
    val cardBlockingStub: CardGrpc.CardBlockingStub
) {
    private companion object : Log

    fun listsCardSync() {
        TODO()
    }

    fun cardBillsSync() {
        TODO()
    }

    fun creditLimitsSync() {
        TODO()
    }
}
