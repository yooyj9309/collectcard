package com.rainist.collectcard.cardcreditlimit

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.cardcreditlimit.dto.CardCreditLimitRequestDataBody
import com.rainist.collectcard.cardcreditlimit.dto.CardCreditLimitRequestDataHeader
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitRequest
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitResponse
import com.rainist.collectcard.cardcreditlimit.dto.toCreditLimitResponseProto
import com.rainist.collectcard.cardcreditlimit.entity.CreditLimitEntity
import com.rainist.collectcard.cardcreditlimit.repository.CreditLimitHistoryRepository
import com.rainist.collectcard.cardcreditlimit.repository.CreditLimitRepository
import com.rainist.collectcard.cardcreditlimit.util.CreditLimitEntityUtil
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.common.exception.CollectcardException
import com.rainist.collectcard.header.HeaderService
import com.rainist.common.log.Log
import com.rainist.common.util.DateTimeUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CardCreditLimitServiceImpl(
    val collectExecutorService: CollectExecutorService,
    val headerService: HeaderService,
    val creditLimitRepository: CreditLimitRepository,
    val creditLimitHistoryRepository: CreditLimitHistoryRepository
) : CardCreditLimitService {

    companion object : Log

    @Transactional
    override fun cardCreditLimit(request: CollectcardProto.GetCreditLimitRequest): CollectcardProto.GetCreditLimitResponse {
        val lastCheckAt = DateTimeUtil.kstNowLocalDateTime()
        val header = headerService.getHeader(request.userId, request.companyId.value)

        return runCatching<ExecutionResponse<CreditLimitResponse>> {
            collectExecutorService.execute(
                Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.creditLimit),
                ExecutionRequest.builder<CreditLimitRequest>()
                    .headers(header)
                    .request(
                        CreditLimitRequest().apply {
                            this.dataHeader = CardCreditLimitRequestDataHeader()
                            this.dataBody = CardCreditLimitRequestDataBody()
                        })
                    .build()
            )
        }.mapCatching { executionResponse ->
            var creditLimitEntity = creditLimitRepository.findCreditLimitEntitiesByBanksaladUserIdAndCardCompanyId(request.userId.toLong(), request.companyId.value) ?: CreditLimitEntity()

            if (executionResponse.response.dataBody == null || executionResponse.response.dataBody?.creditLimitInfo == null)
                throw CollectcardException("DataBody is null")

            val resEntity = CreditLimitEntityUtil.makeCreditLimitEntity(
                lastCheckAt,
                request,
                executionResponse.response.dataBody?.creditLimitInfo!!
            )

            if (CreditLimitEntityUtil.diffCheck(creditLimitEntity, resEntity)) {
                // update
                CreditLimitEntityUtil.copyCreditLimitEntity(lastCheckAt, resEntity, creditLimitEntity)
                creditLimitEntity = creditLimitRepository.save(creditLimitEntity)
                creditLimitHistoryRepository.save(
                    CreditLimitEntityUtil.makeCreditLimitHistoryEntity(creditLimitEntity)
                )
            } else {
                creditLimitEntity.lastCheckAt = lastCheckAt
            }

            executionResponse.response.toCreditLimitResponseProto()
        }.onSuccess { creditLimitResponse ->
            logger.debug("응답 리턴 성공 : {}", creditLimitResponse)
            creditLimitResponse
        }.onFailure {
            throw CollectcardException(it.localizedMessage, it)
        }.getOrThrow()
    }
}
