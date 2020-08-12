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
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.dto.SyncRequest
import com.rainist.collectcard.common.exception.CollectcardException
import com.rainist.collectcard.common.service.ApiLogService
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.util.SyncStatus
import com.rainist.common.log.Log
import com.rainist.common.util.DateTimeUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CardCreditLimitServiceImpl(
    val apiLogService: ApiLogService,
    val headerService: HeaderService,
    val collectExecutorService: CollectExecutorService,
    val creditLimitRepository: CreditLimitRepository,
    val creditLimitHistoryRepository: CreditLimitHistoryRepository
) : CardCreditLimitService {

    companion object : Log

    @Transactional
    @SyncStatus(transactionId = "cardCreditLimit")
    override fun cardCreditLimit(syncRequest: SyncRequest): CreditLimitResponse {

        val lastCheckAt = DateTimeUtil.utcNowLocalDateTime()

        /* request header */
        val header = headerService.makeHeader(syncRequest.banksaladUserId.toString(), syncRequest.organizationId)

        /* request body */
        val creditLimitRequest = CreditLimitRequest().apply {
            this.dataHeader = CardCreditLimitRequestDataHeader()
            this.dataBody = CardCreditLimitRequestDataBody()
        }

        /* Execution Context */
        val executionContext: ExecutionContext = CollectExecutionContext(
            organizationId = syncRequest.organizationId,
            userId = syncRequest.banksaladUserId.toString(),
            startAt = DateTimeUtil.utcNowLocalDateTime()
        )

        // get api call result
        val executionResponse: ExecutionResponse<CreditLimitResponse> = collectExecutorService.execute(
            executionContext,
            Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.creditLimit),
            ExecutionRequest.builder<CreditLimitRequest>()
                .headers(header)
                .request(creditLimitRequest)
                .build()
        )

        // validate
        if (executionResponse.response?.dataBody == null || executionResponse.response?.dataBody?.creditLimitInfo == null)
            throw CollectcardException("DataBody is null")

        // db insert
        var creditLimitEntity = creditLimitRepository.findCreditLimitEntitiesByBanksaladUserIdAndCardCompanyId(
            syncRequest.banksaladUserId.toLong(),
            syncRequest.organizationId
        ) ?: CreditLimitEntity()

        val resEntity = CreditLimitEntityUtil.makeCreditLimitEntity(
            lastCheckAt,
            syncRequest.banksaladUserId.toLong(),
            syncRequest.organizationId,
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
