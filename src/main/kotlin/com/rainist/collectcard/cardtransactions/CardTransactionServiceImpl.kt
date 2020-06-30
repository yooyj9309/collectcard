package com.rainist.collectcard.cardtransactions

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.collect.common.dto.ApiRequest
import com.rainist.collect.common.dto.ApiResponse
import com.rainist.collect.executor.service.CollectExecutorService
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsRequest
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsRequestDataBody
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsRequestDataHeader
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponseDataBody
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
import java.util.concurrent.Executor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@ExperimentalCoroutinesApi
@Service
class CardTransactionServiceImpl(
    val collectExecutorService: CollectExecutorService,
    val listCardRequestValidator: ListCardRequestValidator,
    val validationService: ValidationService,
    val headerService: HeaderService,
    @Qualifier("async-thread") val executor: Executor
) : CardTransactionService {

    companion object : Log {
        const val MAX_MONTH = 12L
        const val DIVISION = 3
    }

    fun execute(header: MutableMap<String, String?>, listTransactionsRequest: ListTransactionsRequest): List<ApiResponse<ListTransactionsResponse>> {
        val startDate = DateTimeUtil.stringToLocalDate(listTransactionsRequest.dataBody?.startAt ?: DateTimeUtil.kstNowLocalDateString("yyyyMMdd"), "yyyyMMdd")
        val endDate = DateTimeUtil.stringToLocalDate(listTransactionsRequest.dataBody?.endAt ?: DateTimeUtil.kstNowLocalDateString("yyyyMMdd"), "yyyyMMdd")

        val searchDateList = DateTimeUtil.splitLocalDateRangeByMonth(startDate, endDate, DIVISION)

        val responseList = runBlocking(executor.asCoroutineDispatcher()) {

            searchDateList.map {
                ListTransactionsRequest().apply {
                    dataHeader = ListTransactionsRequestDataHeader()
                    dataBody = ListTransactionsRequestDataBody().apply {
                        startAt = DateTimeUtil.localDateToString(it.startDate, "yyyyMMdd")
                        endAt = DateTimeUtil.localDateToString(it.endDate, "yyyyMMdd")
                        nextKey = ""
                    }
                }
            }.map {
                async {
                    val res: ApiResponse<ListTransactionsResponse> = collectExecutorService.execute(
                        Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.cardTransaction),
                        ApiRequest.builder<ListTransactionsRequest>()
                            .headers(header)
                            .request(it)
                            .build()
                    )
                    res
                }
            }
        }

        return responseList.map {
            it.getCompleted()
        }
    }

    override fun listTransactions(header: MutableMap<String, String?>, listTransactionsRequest: ListTransactionsRequest): ListTransactionsResponse {

        return kotlin.runCatching {
            val response = execute(header, listTransactionsRequest)

            val headerValidations = response.filter {
                // TODO 예상국 각 금융사별 OK 사인 코드 넣기 ( 제대로 온 내역만 파싱하고 나머지는 로그 알림이 맞지 않나? ) Body Validation 으로 처리 가능 쉐도잉시 정책 적용
                true
            }

            val bodyValidation = headerValidations.flatMap {
                it.response.dataBody?.transactions?.toMutableList() ?: mutableListOf()
            }
            .filter {
                it.cardNumber?.length ?: 0 > 0
            }
            .mapNotNull {
                validationService.validateOrNull(it)
            }
            .toMutableList()

            ListTransactionsResponse().apply {
                this.dataBody = ListTransactionsResponseDataBody().apply {
                    this.transactions = bodyValidation
                }
            }
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
