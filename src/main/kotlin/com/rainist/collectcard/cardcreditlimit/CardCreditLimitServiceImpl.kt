package com.rainist.collectcard.cardcreditlimit

import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.cardcreditlimit.dto.CardCreditLimitRequestDataBody
import com.rainist.collectcard.cardcreditlimit.dto.CardCreditLimitRequestDataHeader
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitRequest
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitResponse
import com.rainist.collectcard.cardcreditlimit.util.CreditLimitEntityUtil
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.common.db.entity.CreditLimitEntity
import com.rainist.collectcard.common.db.repository.CreditLimitHistoryRepository
import com.rainist.collectcard.common.db.repository.CreditLimitRepository
import com.rainist.collectcard.common.exception.CollectcardException
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.util.ExecutionResponseValidator
import com.rainist.collectcard.common.util.SyncStatus
import com.rainist.common.log.Log
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CardCreditLimitServiceImpl(
    val headerService: HeaderService,
    val collectExecutorService: CollectExecutorService,
    val creditLimitRepository: CreditLimitRepository,
    val creditLimitHistoryRepository: CreditLimitHistoryRepository
) : CardCreditLimitService {

    companion object : Log

    @Transactional
    @SyncStatus(transactionId = "cardCreditLimit")
    override fun cardCreditLimit(executionContext: ExecutionContext): CreditLimitResponse {

        val banksaladUserId = executionContext.userId.toLong()
        val lastCheckAt = executionContext.startAt

        /* request header */
        val header = headerService.makeHeader(executionContext.userId, executionContext.organizationId)

        /* request body */
        val creditLimitRequest = CreditLimitRequest().apply {
            this.dataHeader = CardCreditLimitRequestDataHeader()
            this.dataBody = CardCreditLimitRequestDataBody()
        }

        // get api call result
        val executionResponse: ExecutionResponse<CreditLimitResponse> = collectExecutorService.execute(
            executionContext,
            Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.creditLimit),
            ExecutionRequest.builder<CreditLimitRequest>()
                .headers(header)
                .request(creditLimitRequest)
                .build()
        )

        /* check response result */
        ExecutionResponseValidator.validateResponseAndThrow(
            executionResponse,
            executionResponse.response.dataHeader?.resultCode)

        // validate
        if (executionResponse.response?.dataBody == null || executionResponse.response?.dataBody?.creditLimitInfo == null)
            throw CollectcardException("DataBody is null")

        // db insert
        var creditLimitEntity = creditLimitRepository.findCreditLimitEntitiesByBanksaladUserIdAndCardCompanyId(
            banksaladUserId,
            executionContext.organizationId
        ) ?: CreditLimitEntity()

        val resEntity = CreditLimitEntityUtil.makeCreditLimitEntity(
            lastCheckAt,
            banksaladUserId,
            executionContext.organizationId,
            executionResponse.response?.dataBody?.creditLimitInfo
        )

        if (CreditLimitEntityUtil.isUpdated(creditLimitEntity, resEntity)) {
            // update
            CreditLimitEntityUtil.copyCreditLimitEntity(lastCheckAt, resEntity, creditLimitEntity)
            creditLimitEntity = creditLimitRepository.save(creditLimitEntity)
            creditLimitHistoryRepository.save(
                CreditLimitEntityUtil.makeCreditLimitHistoryEntity(creditLimitEntity)
            )
        } else {
            creditLimitEntity.lastCheckAt = lastCheckAt
        }

        // return
        return executionResponse.response
    }
}
