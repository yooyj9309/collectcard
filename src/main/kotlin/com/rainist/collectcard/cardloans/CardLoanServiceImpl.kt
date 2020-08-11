package com.rainist.collectcard.cardloans

import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.cardloans.dto.ListLoansRequest
import com.rainist.collectcard.cardloans.dto.ListLoansRequestDataBody
import com.rainist.collectcard.cardloans.dto.ListLoansRequestDataHeader
import com.rainist.collectcard.cardloans.dto.ListLoansResponse
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.common.db.entity.CardLoanEntity
import com.rainist.collectcard.common.db.entity.CardLoanHistoryEntity
import com.rainist.collectcard.common.db.entity.makeCardLoanEntity
import com.rainist.collectcard.common.db.entity.makeCardLoanHistoryEntity
import com.rainist.collectcard.common.db.repository.CardLoanHistoryRepository
import com.rainist.collectcard.common.db.repository.CardLoanRepository
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.dto.SyncRequest
import com.rainist.collectcard.common.service.ApiLogService
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.util.SyncStatus
import com.rainist.common.log.Log
import com.rainist.common.util.DateTimeUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CardLoanServiceImpl(
    val apiLogService: ApiLogService,
    val headerService: HeaderService,
    val collectExecutorService: CollectExecutorService,
    val cardLoanRepository: CardLoanRepository,
    val cardLoanHistoryRepository: CardLoanHistoryRepository
) : CardLoanService {

    companion object : Log

    @Transactional
    @SyncStatus(transactionId = "cardLoans")
    override fun listCardLoans(syncRequest: SyncRequest): ListLoansResponse {
        /* request header */
        val lastCheckAt = DateTimeUtil.getLocalDateTime()
        val header = headerService.makeHeader(syncRequest.banksaladUserId.toString(), syncRequest.organizationId)

        /* request body */
        val listLoansRequest = ListLoansRequest().apply {
            this.dataHeader = ListLoansRequestDataHeader()
            this.dataBody = ListLoansRequestDataBody()
        }

        /* Execution Context */
        val executionContext: ExecutionContext = CollectExecutionContext(
            organizationId = syncRequest.organizationId,
            userId = syncRequest.banksaladUserId.toString(),
            startAt = DateTimeUtil.utcNowLocalDateTime()
        )

        /* service logic */
        val res: ExecutionResponse<ListLoansResponse> = collectExecutorService.execute(
            executionContext,
            Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.loan),
            ExecutionRequest.builder<ListLoansRequest>()
                .headers(header)
                .request(listLoansRequest)
                .build()
        )

        /* validate logic */
        if (res.httpStatusCode != 200) {
            // TODO 예상국 기존 에러 처리 로직 확인해서 반영하기
        }

        /* db insert */
        res.response.dataBody?.loans?.forEach { loan ->

            loan.loanId?.let {
                cardLoanRepository.findByBanksaladUserIdAndCardCompanyIdAndCardCompanyLoanId(
                    syncRequest.banksaladUserId.toLong(),
                    syncRequest.organizationId,
                    loan.loanId
                )
            }
                ?.let { cardLoanEntity ->
                    // update
                    val prevLastCheckAt = cardLoanEntity.lastCheckAt
                    val prevUpdatedAt = cardLoanEntity.updatedAt

                    val bodyEntity = cardLoanEntity.makeCardLoanEntity(
                        prevLastCheckAt,
                        syncRequest.banksaladUserId,
                        syncRequest.organizationId,
                        loan
                    )
                    val saveEntity = cardLoanRepository.saveAndFlush(bodyEntity)

                    // history insert
                    if (true == saveEntity.updatedAt?.isAfter(prevUpdatedAt)) {
                        cardLoanHistoryRepository.save(CardLoanHistoryEntity().makeCardLoanHistoryEntity(saveEntity))
                    }

                    saveEntity.lastCheckAt = DateTimeUtil.utcNowLocalDateTime()
                    cardLoanRepository.save(saveEntity)
                }
                ?: kotlin.run {
                    // only insert
                    val loanEntity = CardLoanEntity().makeCardLoanEntity(
                        lastCheckAt,
                        syncRequest.banksaladUserId,
                        syncRequest.organizationId,
                        loan
                    )

                    cardLoanRepository.save(loanEntity).let { cardLoanEntity ->
                        val history = CardLoanHistoryEntity().makeCardLoanHistoryEntity(cardLoanEntity)
                        cardLoanHistoryRepository.save(history)
                    }
                }
        }

        return ListLoansResponse().apply {
            this.dataBody = res.response.dataBody
        }
    }
}
