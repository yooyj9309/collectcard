package com.rainist.collectcard.grpc.client

import com.github.banksalad.idl.apis.v1.loan.LoanGrpc
import com.rainist.common.log.Log
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("production", "development", "staging", "local")
class LoanClientService(
    @Qualifier("loanBlockingStub")
    val loanBlockingStub: LoanGrpc.LoanBlockingStub
) {
    private companion object : Log

    fun cardLoansSync() {
        TODO()
    }
}
