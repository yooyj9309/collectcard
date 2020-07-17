package com.rainist.collectcard.cardloans

import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.cardloans.dto.ListLoansRequest
import com.rainist.collectcard.cardloans.dto.ListLoansRequestDataBody
import com.rainist.collectcard.cardloans.dto.ListLoansRequestDataHeader
import com.rainist.collectcard.cardloans.dto.ListLoansResponse
import com.rainist.collectcard.cardloans.validation.ListCardLoansRequestValidator
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.header.HeaderService
import com.rainist.common.log.Log
import com.rainist.common.service.ValidationService
import org.springframework.stereotype.Service

@Service
class CardLoanServiceImpl(
    val listCardLoansRequestValidator: ListCardLoansRequestValidator,
    val headerService: HeaderService,
    val validationService: ValidationService,
    val collectExecutorService: CollectExecutorService
) : CardLoanService {

    companion object : Log

    override fun listCardLoans(banksaladUserId: String, organizationId: String): ListLoansResponse {
        /* request header */
        val header = headerService.makeHeader(banksaladUserId, organizationId)

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
            //
        }

        /* db insert */
        res.response.dataBody?.loans?.forEach { loan ->
            // logic
        }

        return ListLoansResponse().apply {
            this.dataBody = res.response.dataBody
        }
    }
}
