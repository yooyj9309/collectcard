package com.rainist.collectcard.cardloans

import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.cardloans.dto.ListLoansRequest
import com.rainist.collectcard.cardloans.dto.ListLoansRequestDataBody
import com.rainist.collectcard.cardloans.dto.ListLoansRequestDataHeader
import com.rainist.collectcard.cardloans.dto.ListLoansResponse
import com.rainist.collectcard.cardloans.util.CardLoanUtil
import com.rainist.collectcard.cardloans.validation.ListCardLoansRequestValidator
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.common.db.repository.CardLoanHistoryRepository
import com.rainist.collectcard.common.db.repository.CardLoanRepository
import com.rainist.collectcard.common.service.CardOrganization
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.common.log.Log
import com.rainist.common.service.ValidationService
import com.rainist.common.util.DateTimeUtil
import org.springframework.stereotype.Service

@Service
class CardLoanServiceImpl(
    val listCardLoansRequestValidator: ListCardLoansRequestValidator,
    val headerService: HeaderService,
    val validationService: ValidationService,
    val collectExecutorService: CollectExecutorService,
    val cardLoanRepository: CardLoanRepository,
    val cardLoanHistoryRepository: CardLoanHistoryRepository
) : CardLoanService {

    companion object : Log

    override fun listCardLoans(banksaladUserId: String, organization: CardOrganization): ListLoansResponse {
        /* request header */
        val lastCheckAt = DateTimeUtil.getLocalDateTime()
        val header = headerService.makeHeader(banksaladUserId, organization)

        /* request body */
        val listLoansRequest = ListLoansRequest().apply {
            this.dataHeader = ListLoansRequestDataHeader()
            this.dataBody = ListLoansRequestDataBody()
        }

        /* service logic */
        val res: ExecutionResponse<ListLoansResponse> = collectExecutorService.execute(
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
            if (loan.loanId != null) {
                val cardLoanEntity = cardLoanRepository.findByBanksaladUserIdAndCardCompanyIdAndCardCompanyLoanId(
                    banksaladUserId.toLong(),
                    organization.organizationId,
                    loan.loanId
                )

                var bodyEntity = CardLoanUtil.makeCardLoanEntity(
                    banksaladUserId,
                    organization.organizationId,
                    loan
                )

                // only save(insert)
                if (cardLoanEntity == null) {
                    bodyEntity = cardLoanRepository.save(bodyEntity)
                    cardLoanHistoryRepository.save(CardLoanUtil.makeCardLoanHistoryEntity(
                        lastCheckAt, bodyEntity))
                }
                // update
                else {
                    if (CardLoanUtil.diffCheck(cardLoanEntity, bodyEntity)) {
                        // update
                        CardLoanUtil.copyCardLoanEntity(bodyEntity, cardLoanEntity)
                        cardLoanRepository.save(cardLoanEntity)
                        cardLoanHistoryRepository.save(CardLoanUtil.makeCardLoanHistoryEntity(lastCheckAt, cardLoanEntity))
                    }
                }
            }
        }

        return ListLoansResponse().apply {
            this.dataBody = res.response.dataBody
        }
    }
}
