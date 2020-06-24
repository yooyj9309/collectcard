package com.rainist.collectcard.cardtransactions

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.collect.common.dto.ApiRequest
import com.rainist.collect.common.dto.ApiResponse
import com.rainist.collect.executor.service.CollectExecutorService
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsRequest
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsRequestDataBody
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsRequestDataHeader
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse
import com.rainist.collectcard.cardtransactions.dto.toListCardsReponseProto
import com.rainist.collectcard.cardtransactions.validation.ListCardRequestValidator
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.header.ShinhancardHeaderService
import com.rainist.common.exception.UnknownException
import com.rainist.common.log.Log
import com.rainist.common.model.ObjectOf
import com.rainist.common.service.ValidationService
import com.rainist.common.util.DateTimeUtil
import org.springframework.stereotype.Service

@Service
class CardTransactionServiceImpl(
    val collectExecutorService: CollectExecutorService,
    val listCardRequestValidator: ListCardRequestValidator,
    val validationService: ValidationService,
    val shinhancardHeaderService: ShinhancardHeaderService
) : CardTransactionService {

    companion object : Log

    fun execute(header: MutableMap<String, String>, listTransactionsRequest: ListTransactionsRequest): ApiResponse<ListTransactionsResponse> {
        return collectExecutorService.execute(
            Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.cardTransaction),
            ApiRequest.builder<ListTransactionsRequest>()
                .headers(header)
                .request(listTransactionsRequest)
                .build()
        )
    }

    override fun listTransactions(header: MutableMap<String, String>, listTransactionsRequest: ListTransactionsRequest): ListTransactionsResponse {

        return kotlin.runCatching {
            val res = execute(header, listTransactionsRequest)
            validationService.validateOrThrows(res.response)
        }
        .onFailure {
            logger.withFieldError("listTransactions", it.localizedMessage, it)
        }
        .getOrThrow()
    }

    // Asset Gateway req
    fun listTransactions(request: CollectcardProto.ListCardTransactionsRequest): CollectcardProto.ListCardTransactionsResponse {

        return kotlin.runCatching {
            takeIf {
                listCardRequestValidator.isValid(ObjectOf(request))
            }
            ?.let {
                ListTransactionsRequest().apply {
                    dataHeader = ListTransactionsRequestDataHeader()
                    dataBody = ListTransactionsRequestDataBody().apply {
                        startAt = let { DateTimeUtil.kstNowLocalDate().minusMonths(6L) }.let { DateTimeUtil.localDateToString(it, "yyyyMMdd") }
                        endAt = DateTimeUtil.kstNowLocalDateString("yyyyMMdd")
                        nextKey = ""
                    }
                }
            }
            ?.let {
                listTransactions(shinhancardHeaderService.getHeader(request.userId, request.companyId.value), it)
            }
            ?.toListCardsReponseProto()
            ?: kotlin.run {
                throw UnknownException()
            }
        }
        .onFailure {
            logger.withFieldError("listTransactions", it.localizedMessage, it)
        }
        .getOrThrow()
    }
}
