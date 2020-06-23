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
import com.rainist.common.exception.UnknownException
import com.rainist.common.log.Log
import com.rainist.common.model.ObjectOf
import com.rainist.common.service.ValidationService
import com.rainist.common.util.DateTimeUtil
import java.time.ZoneId
import org.springframework.stereotype.Service

@Service
class CardTransactionServiceImpl(
    val collectExecutorService: CollectExecutorService,
    val listCardRequestValidator: ListCardRequestValidator,
    val validationService: ValidationService
) : CardTransactionService {

    companion object : Log

    // TODO : implement proper header fields
    fun makeHeader(): MutableMap<String, String> {
        return mutableMapOf<String, String>(
            "Authorization" to "bearer",
            "userDeviceId" to "4b7626ac-a43a-4dd3-905f-66e31aa5c2b3",
            "deviceOs" to "Android"
        )
    }

    override fun listTransactions(listTransactionsRequest: ListTransactionsRequest): ListTransactionsResponse {

        return kotlin.runCatching {
            val res: ApiResponse<ListTransactionsResponse> = collectExecutorService.execute(
                Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.cardTransaction),
                ApiRequest.builder<ListTransactionsRequest>()
                    .headers(makeHeader())
                    .request(listTransactionsRequest)
                    .build()
            )

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
                // TODO 예상국 날짜 전체 기간중 async 하게 req 변경

                ListTransactionsRequest().apply {
                    dataHeader = ListTransactionsRequestDataHeader()
                    dataBody = ListTransactionsRequestDataBody().apply {
                        // TODO 예상국 kotlin banksalad 에 util function 만들기
                        startAt = takeIf { request.hasFromMs() }
                            ?.let { request.fromMs.value }
                            ?.let { DateTimeUtil.epochMilliSecondToDatetime(it, ZoneId.of("Asia/Seoul")) }
                            ?.let { DateTimeUtil.datetimeToDateString(it, "yyyyMMdd") }
                            ?: kotlin.run {
                                // TODO 예상국 default 최대 기간 넣기
                                ""
                            }
                        endAt = DateTimeUtil.kstNowLocalDateString("yyyyMMdd")
                        nextKey = ""
                    }
                }
            }
            ?.let {
                listTransactions(it)
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
