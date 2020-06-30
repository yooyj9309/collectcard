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
import com.rainist.collectcard.header.HeaderService
import com.rainist.collectcard.header.dto.HeaderInfo
import com.rainist.common.exception.UnknownException
import com.rainist.common.log.Log
import com.rainist.common.model.ObjectOf
import com.rainist.common.service.ValidationService
import com.rainist.common.util.DateTimeUtil
import java.time.LocalDate
import org.springframework.stereotype.Service

@Service
class CardTransactionServiceImpl(
    val collectExecutorService: CollectExecutorService,
    val listCardRequestValidator: ListCardRequestValidator,
    val validationService: ValidationService,
    val headerService: HeaderService
) : CardTransactionService {

    companion object : Log {
        const val MAX_MONTH = 6L
    }

    fun execute(header: MutableMap<String, String?>, listTransactionsRequest: ListTransactionsRequest): ApiResponse<ListTransactionsResponse> {
        return collectExecutorService.execute(
            Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.cardTransaction),
            ApiRequest.builder<ListTransactionsRequest>()
                .headers(header)
                .request(listTransactionsRequest)
                .build()
        )
    }

    override fun listTransactions(header: MutableMap<String, String?>, listTransactionsRequest: ListTransactionsRequest): ListTransactionsResponse {

        return kotlin.runCatching {
            val res = execute(header, listTransactionsRequest)

            // TODO 금융사의 response code 에 따른 exception 분기처리
            res.response.dataBody?.transactions
            ?.forEach {
                validationService.validateOrThrows(it)
            }

            res.response
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
                        startAt = takeIf { request.hasFromMs() }
                            ?.let { DateTimeUtil.epochMilliSecondToKSTLocalDateTime(request.fromMs.value) }
                            ?.let { localDateTime -> LocalDate.of(localDateTime.year, localDateTime.month, localDateTime.dayOfMonth) }
                            ?.let { DateTimeUtil.localDateToString(it, "yyyyMMdd") }
                            ?: kotlin.run {
                                DateTimeUtil.kstNowLocalDate().minusMonths(MAX_MONTH)
                                    .let { DateTimeUtil.localDateToString(it, "yyyyMMdd") }
                            }
                        endAt = DateTimeUtil.kstNowLocalDateString("yyyyMMdd")
                        nextKey = ""
                    }
                }
            }
            ?.let { listTransactionRequest ->
                HeaderInfo().apply {
                    this.banksaladUserId = request.userId
                    this.organizationObjectId = request.companyId.value
                }.let { headerInfo ->
                    headerService.getHeader(headerInfo)
                }.let { header ->
                    listTransactions(header, listTransactionRequest)
                }
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
