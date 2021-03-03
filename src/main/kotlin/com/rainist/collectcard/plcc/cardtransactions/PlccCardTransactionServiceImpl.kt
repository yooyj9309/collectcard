package com.rainist.collectcard.plcc.cardtransactions

import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.service.OrganizationService
import com.rainist.collectcard.plcc.cardtransactions.dto.PlccCardTransactionRequest
import com.rainist.collectcard.plcc.cardtransactions.dto.PlccCardTransactionRequestDataBody
import com.rainist.collectcard.plcc.cardtransactions.dto.PlccCardTransactionRequestDataHeader
import com.rainist.collectcard.plcc.cardtransactions.dto.PlccCardTransactionResponse
import com.rainist.common.service.ValidationService
import org.springframework.http.MediaType
import org.springframework.stereotype.Service

@Service
class PlccCardTransactionServiceImpl(
    val headerService: HeaderService,
    val organizationService: OrganizationService,
    val collectExecutorService: CollectExecutorService,
    val validationService: ValidationService

) : PlccCardTransactionService {

    override fun plccCardTransactions(executionContext: CollectExecutionContext): PlccCardTransactionResponse {
        val banksaladUserId = executionContext.userId.toLong()
        val organizationId = executionContext.organizationId
        val organization = organizationService.getOrganizationByObjectId(organizationId)

        val executions = Executions.valueOf(
            BusinessType.card,
            Organization.lottecard,
            Transaction.plccCardTransaction
        )

        val request = ExecutionRequest.builder<PlccCardTransactionRequest>()
            .headers(
                headerService.makeHeader(MediaType.APPLICATION_JSON_VALUE)
            )
            .request(
                PlccCardTransactionRequest().apply {
                this.dataHeader = PlccCardTransactionRequestDataHeader()
                this.dataBody = PlccCardTransactionRequestDataBody().apply {
                        // TODO STEVE GRPC Request Mapping
                        this.cardNumber = ""
                        this.inquiryYearMonth = ""
                        this.inquiryCode = organization.screenInquiryCode
                        this.productCode = organization.benefitProductCode
                    }
                }
            )
            .build()

        // Req
        val executionResponse: ExecutionResponse<PlccCardTransactionResponse> = collectExecutorService.execute(executionContext, executions, request)

        // Validation
        val transactions = executionResponse.response?.dataBody
            .let { plccCardTransactionResponseDataBody ->
                plccCardTransactionResponseDataBody?.transactionList
            }
            ?.mapNotNull { plccCardTransaction ->
                validationService.validateOrNull(plccCardTransaction)
            }
            ?.toList()
            ?: listOf()

        // TODO DB SAVE

        return executionResponse.response.apply {
            dataBody?.transactionList = transactions
        }
    }
}
