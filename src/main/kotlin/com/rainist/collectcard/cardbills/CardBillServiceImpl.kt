package com.rainist.collectcard.cardbills

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.cardbills.dto.ListCardBillsRequest
import com.rainist.collectcard.cardbills.dto.ListCardBillsRequestDataBody
import com.rainist.collectcard.cardbills.dto.ListCardBillsRequestDataHeader
import com.rainist.collectcard.cardbills.dto.ListCardBillsResponse
import com.rainist.collectcard.cardbills.dto.toListCardBillsResponseProto
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.common.exception.CollectcardException
import com.rainist.collectcard.common.organization.Organizations
import com.rainist.collectcard.header.HeaderService
import com.rainist.collectcard.header.dto.HeaderInfo
import com.rainist.common.log.Log
import com.rainist.common.util.DateTimeUtil
import java.time.LocalDate
import org.springframework.stereotype.Service

@Service
class CardBillServiceImpl(
    val collectExecutorService: CollectExecutorService,
    val headerService: HeaderService
) : CardBillService {

    companion object : Log {
        const val DEFAULT_MAX_MONTH = 6L
    }

    override fun listUserCardBills(
        header: MutableMap<String, String?>,
        listCardBillsRequest: ListCardBillsRequest
    ): ListCardBillsResponse {
        val cardBillsResponse = this.listCardBills(header, listCardBillsRequest)
        val cardExpectedBillsResponse = this.listCardBills(header, listCardBillsRequest)

        cardBillsResponse.dataBody?.cardBills?.addAll(cardExpectedBillsResponse.dataBody?.cardBills ?: mutableListOf())

        return cardBillsResponse
    }

    fun listUserCardBillsExpected(
        header: MutableMap<String, String?>,
        listCardBillsRequest: ListCardBillsRequest
    ): ListCardBillsResponse {

        return runCatching<ExecutionResponse<ListCardBillsResponse>> {
            collectExecutorService.execute(
                Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.billTransactionExpected),
                ExecutionRequest.builder<ListCardBillsRequest>()
                    .headers(header)
                    .request(listCardBillsRequest)
                    .build()
            )
        }.onFailure {
            logger.error("Failed to retrieve bills expected list: ${it.message}")
            throw CollectcardException("exception", it)
        }.getOrThrow().response
    }

    fun listCardBills(
        header: MutableMap<String, String?>,
        listCardBillsRequest: ListCardBillsRequest
    ): ListCardBillsResponse {

        return runCatching<ExecutionResponse<ListCardBillsResponse>> {
            collectExecutorService.execute(
                Executions.valueOf(BusinessType.card, Organization.shinhancard, Transaction.cardbills),
                ExecutionRequest.builder<ListCardBillsRequest>()
                    .headers(header)
                    .request(listCardBillsRequest)
                    .build()
            )
        }.onFailure {
            logger.error("Failed to retrieve bills list: ${it.message}")
            throw CollectcardException("exception", it)
        }.getOrThrow().response
    }

    fun listUserCardBills(request: CollectcardProto.ListCardBillsRequest): CollectcardProto.ListCardBillsResponse {
        return kotlin.runCatching {
            ListCardBillsRequest().apply {
                this.dataHeader = ListCardBillsRequestDataHeader()
                this.dataBody = ListCardBillsRequestDataBody().apply {
                    this.startAt = takeIf { request.hasFromMs() }
                        ?.let { DateTimeUtil.epochMilliSecondToKSTLocalDateTime(request.fromMs.value) }
                        ?.let { localDateTime ->
                            LocalDate.of(
                                localDateTime.year,
                                localDateTime.month,
                                localDateTime.dayOfMonth
                            )
                        }
                        ?.let { DateTimeUtil.localDateToString(it, "yyyyMMdd") }
                        ?: kotlin.run {
                            val cardOrganization = Organizations.valueOf(request.companyId.value)
                            DateTimeUtil.kstNowLocalDate().minusMonths(
                                cardOrganization?.maxMonth
                                    ?: CardBillServiceImpl.DEFAULT_MAX_MONTH
                            )
                                .let {
                                    DateTimeUtil.localDateToString(it, "yyyyMMdd")
                                }
                        }
                }
            }
                .let { listCardBillsRequest ->

                    HeaderInfo().apply {
                        this.banksaladUserId = request.userId
                        this.organizationObjectid = request.companyId.value
                        this.clientId = Organizations.valueOf(request.companyId.value)?.clientId
                    }.let { headerInfo ->
                        headerService.getHeader(headerInfo)
                    }.let { header ->
                        listCardBills(header, listCardBillsRequest)
                    }
                }.toListCardBillsResponseProto()
        }.getOrThrow()
    }
}
