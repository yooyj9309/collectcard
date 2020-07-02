package com.rainist.collectcard.cardloans

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.collect.common.dto.ApiRequest
import com.rainist.collect.common.dto.ApiResponse
import com.rainist.collect.executor.service.CollectExecutorService
import com.rainist.collectcard.cardloans.dto.ListLoansRequest
import com.rainist.collectcard.cardloans.dto.ListLoansRequestDataBody
import com.rainist.collectcard.cardloans.dto.ListLoansRequestDataHeader
import com.rainist.collectcard.cardloans.dto.ListLoansResponse
import com.rainist.collectcard.cardloans.dto.ListLoansResponseDataBody
import com.rainist.collectcard.cardloans.dto.toListCardLoansResponseProto
import com.rainist.collectcard.cardloans.validation.ListCardLoansRequestValidator
import com.rainist.collectcard.cardtransactions.CardTransactionServiceImpl.Companion.withFieldError
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.common.organization.Organizations
import com.rainist.collectcard.header.HeaderService
import com.rainist.collectcard.header.dto.HeaderInfo
import com.rainist.common.log.Log
import com.rainist.common.model.ObjectOf
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

    override fun listCardLoans(header: MutableMap<String, String?>, listLoansRequest: ListLoansRequest): ListLoansResponse? {
        return kotlin.runCatching {
            let {
                validationService.validateOrThrows(listLoansRequest.dataBody)
            }
            ?.let {
                val res: ApiResponse<ListLoansResponse> = collectExecutorService.execute(
                    Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.loan),
                    ApiRequest.builder<ListLoansRequest>()
                        .headers(header)
                        .request(listLoansRequest)
                        .build()
                )
                res
            }
            ?.takeIf {
                it.httpStatusCode == 200 // TODO 예상국 기존 에러 처리 로직 확인해서 반영하기
            }
            ?.let {
                it.response.dataBody?.loans
            }
            ?.mapNotNull {
                validationService.validateOrNull(it)
            }
            ?.toMutableList()
            ?.let {
                ListLoansResponse().apply {
                    this.dataBody = ListLoansResponseDataBody().apply {
                        this.loans = it
                    }
                }
            }
            ?: kotlin.run {
                throw Exception("listCardLoans Error")
            }
        }
        .onFailure {
            logger.withFieldError("ListCardLoansError", it.localizedMessage, it)
        }
        .getOrThrow()
    }

    override fun listCardLoans(listCardLoansRequest: CollectcardProto.ListCardLoansRequest): CollectcardProto.ListCardLoansResponse? {

        return kotlin.runCatching {
            takeIf {
                listCardLoansRequestValidator.isValid(ObjectOf(listCardLoansRequest))
            }
            ?.let {
                ListLoansRequest().apply {
                    this.dataHeader = ListLoansRequestDataHeader()
                    this.dataBody = ListLoansRequestDataBody()
                }
            }
            ?.let { listLoansRequest ->
                HeaderInfo().apply {
                    this.banksaladUserId = listCardLoansRequest.userId
                    this.organizationObjectid = listCardLoansRequest.companyId.value
                    this.clientId = Organizations.valueOf(listCardLoansRequest.companyId.value)?.clientId
                }.let { headerInfo ->
                    headerService.getHeader(headerInfo)
                }.let { header ->
                    listCardLoans(header, listLoansRequest)
                }
            }
            ?.toListCardLoansResponseProto()
            ?: kotlin.run {
                throw Exception("listCardLoans Error")
            }
        }
        .onFailure {
            logger.withFieldError("ListCardLoansError", it.localizedMessage, it)
        }
        .getOrThrow()
    }
}
