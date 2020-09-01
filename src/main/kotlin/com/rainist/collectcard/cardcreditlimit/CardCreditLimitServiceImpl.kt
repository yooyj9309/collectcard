package com.rainist.collectcard.cardcreditlimit

import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.cardcreditlimit.dto.CardCreditLimitRequestDataBody
import com.rainist.collectcard.cardcreditlimit.dto.CardCreditLimitRequestDataHeader
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitRequest
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitResponse as CreditLimitResponse
import com.rainist.collectcard.cardcreditlimit.util.CreditLimitEntityUtil
import com.rainist.collectcard.cardcreditlimit.validation.CreditLimitResponseValidator
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.common.db.entity.CreditLimitEntity
import com.rainist.collectcard.common.db.repository.CreditLimitHistoryRepository
import com.rainist.collectcard.common.db.repository.CreditLimitRepository
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.service.ExecutionResponseValidateService
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.service.UserSyncStatusService
import com.rainist.common.log.Log
import com.rainist.common.model.ObjectOf
import com.rainist.common.util.DateTimeUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CardCreditLimitServiceImpl(
    val headerService: HeaderService,
    val userSyncStatusService: UserSyncStatusService,
    val executionResponseValidateService: ExecutionResponseValidateService,
    val collectExecutorService: CollectExecutorService,
    val creditLimitRepository: CreditLimitRepository,
    val creditLimitHistoryRepository: CreditLimitHistoryRepository,
    val creditLimitResponseValidator: CreditLimitResponseValidator
) : CardCreditLimitService {

    companion object : Log

    @Transactional
    override fun cardCreditLimit(executionContext: CollectExecutionContext): CreditLimitResponse {
        val banksaladUserId = executionContext.userId.toLong()
        val now = DateTimeUtil.utcNowLocalDateTime()

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

        // validate and db insert
        if (creditLimitResponseValidator.isValid(ObjectOf(executionResponse.response))) {
            var creditLimitEntity = creditLimitRepository.findCreditLimitEntitiesByBanksaladUserIdAndCardCompanyId(
                banksaladUserId,
                executionContext.organizationId
            ) ?: CreditLimitEntity()

            val resEntity = CreditLimitEntityUtil.makeCreditLimitEntity(
                now,
                banksaladUserId,
                executionContext.organizationId,
                executionResponse.response?.dataBody?.creditLimitInfo
            )

            if (CreditLimitEntityUtil.isUpdated(creditLimitEntity, resEntity)) {
                // update
                CreditLimitEntityUtil.copyCreditLimitEntity(now, resEntity, creditLimitEntity)
                creditLimitEntity = creditLimitRepository.save(creditLimitEntity)
                creditLimitHistoryRepository.save(
                    CreditLimitEntityUtil.makeCreditLimitHistoryEntity(creditLimitEntity)
                )
            } else {
                creditLimitEntity.lastCheckAt = now
            }
        }

        /* check response result */
        if (executionResponseValidateService.validate(executionContext, executionResponse)) {
            userSyncStatusService.updateUserSyncStatus(
                banksaladUserId,
                executionContext.organizationId,
                Transaction.creditLimit.name,
                DateTimeUtil.utcLocalDateTimeToEpochMilliSecond(now))
        }

        // return
        return executionResponse.response ?: CreditLimitResponse()
    }
}
