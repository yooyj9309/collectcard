package com.rainist.collectcard.cardcreditlimit

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.card.CardsException
import com.rainist.collectcard.cardcreditlimit.dto.CardCreditLimitRequestDataBody
import com.rainist.collectcard.cardcreditlimit.dto.CardCreditLimitRequestDataHeader
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitRequest
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitResponse
import com.rainist.collectcard.cardcreditlimit.dto.toCreditLimitResponseProto
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.header.HeaderService
import com.rainist.common.log.Log
import org.springframework.stereotype.Service

@Service
class CardCreditLimitServiceImpl(
    val collectExecutorService: CollectExecutorService,
    val headerService: HeaderService
) : CardCreditLimitService {

    companion object : Log

    override fun cardCreditLimit(request: CollectcardProto.GetCreditLimitRequest): CollectcardProto.GetCreditLimitResponse {
        return runCatching<ExecutionResponse<CreditLimitResponse>> {
            collectExecutorService.execute(
                Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.creditLimit),
                ExecutionRequest.builder<CreditLimitRequest>()
                    .headers(headerService.getHeader(request.userId, request.companyId.value))
                    .request(
                        CreditLimitRequest().apply {
                            this.dataHeader = CardCreditLimitRequestDataHeader()
                            this.dataBody = CardCreditLimitRequestDataBody()
                        })
                    .build()
            )
        }.mapCatching { executionResponse ->
            executionResponse.response.toCreditLimitResponseProto()
        }.onSuccess { creditLimitResponse ->
            logger.debug("응답 리턴 성공 : {}", creditLimitResponse)
            creditLimitResponse
        }.onFailure {
            throw CardsException(it.localizedMessage, it)
        }.getOrThrow()
    }
}
