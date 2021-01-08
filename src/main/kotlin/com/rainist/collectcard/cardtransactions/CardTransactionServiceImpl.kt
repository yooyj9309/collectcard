package com.rainist.collectcard.cardtransactions

import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.cardtransactions.dto.CardTransaction
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsRequest
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsRequestDataBody
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsRequestDataHeader
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponseDataBody
import com.rainist.collectcard.cardtransactions.util.CardTransactionUtil
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.common.db.repository.CardTransactionRepository
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.service.ExecutionResponseValidateService
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.service.OrganizationService
import com.rainist.collectcard.common.service.UserSyncStatusService
import com.rainist.collectcard.common.util.CustomStringUtil
import com.rainist.common.log.Log
import com.rainist.common.service.ValidationService
import com.rainist.common.util.DateTimeUtil
import java.time.LocalDateTime
import java.util.concurrent.Executor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@ExperimentalCoroutinesApi
@Service
class CardTransactionServiceImpl(
    val headerService: HeaderService,
    val userSyncStatusService: UserSyncStatusService,
    val executionResponseValidateService: ExecutionResponseValidateService,
    val collectExecutorService: CollectExecutorService,
    val validationService: ValidationService,
    val cardTransactionRepository: CardTransactionRepository,
    val organizationService: OrganizationService,
    @Qualifier("async-thread") val executor: Executor,
    @Value("\${shinhancard.organizationId}") val shinhancardOrganizationId: String
) : CardTransactionService {

    companion object : Log

    @Transactional
    override fun listTransactions(executionContext: CollectExecutionContext, now: LocalDateTime): ListTransactionsResponse {
        val banksaladUserId = executionContext.userId.toLong()
        val organizationId = executionContext.organizationId
        /* request header */
        val header = headerService.makeHeader(executionContext.userId, executionContext.organizationId)

        /* request body */
        val request = ListTransactionsRequest().apply {
            this.dataHeader = ListTransactionsRequestDataHeader()
            this.dataBody = ListTransactionsRequestDataBody().apply {
                this.startAt = getStartAt(executionContext)
                this.endAt = DateTimeUtil.kstNowLocalDateString("yyyyMMdd")
            }
        }

        logger.With("banksaladUserId", executionContext.userId).With("startAt", request.dataBody?.startAt
            ?: "startAtNull").Warn("")

        val executionResponses = getListTransactionsByDivision(executionContext, header, request)

        val transactions = executionResponses.flatMap {
            it.response?.dataBody?.transactions ?: mutableListOf()
        }
            .filter {
                it.cardNumber?.length ?: 0 > 0 // 신한카드 처럼 list 갯수 맞추기 위해 공백으로 넣은 쓰레기 데이터 제거
            }
            .mapNotNull {
                validationService.validateOrNull(it)
            }
            .toMutableList()

        /* convert type and format if necessary */
        transactions?.map { transaction ->
            transaction.apply {
                this.cardNumber = this.cardNumber?.replace("-", "")?.trim()
            }
        }

        // db insert
        transactions.forEach { cardTransaction ->
            if (shinhancardOrganizationId == executionContext.organizationId) {
                val code = cardTransaction.currencyCode.replace(" ", "").trim()
                cardTransaction.currencyCode = CardTransactionUtil.currencyCodeMap[code] ?: code
            }

            val cardTransactionEntity =
                cardTransactionRepository.findByApprovalYearMonthAndBanksaladUserIdAndAndCardCompanyIdAndCardCompanyCardIdAndApprovalNumberAndApprovalDayAndApprovalTime(
                    cardTransaction.approvalDay?.substring(0, 6),
                    banksaladUserId,
                    executionContext.organizationId,
                    cardTransaction.cardCompanyCardId,
                    cardTransaction.approvalNumber,
                    cardTransaction.approvalDay,
                    cardTransaction.approvalTime
                )

            if (cardTransactionEntity == null) {
                cardTransactionRepository.save(
                    CardTransactionUtil.makeCardTransactionEntity(
                        banksaladUserId,
                        executionContext.organizationId,
                        cardTransaction
                    )
                )
            }
        }

        userSyncStatusService.upsertUserSyncStatus(
            banksaladUserId,
            executionContext.organizationId,
            Transaction.cardTransaction.name,
            DateTimeUtil.utcLocalDateTimeToEpochMilliSecond(now),
            executionResponseValidateService.validate(executionContext, executionResponses)
        )

        // 신한카드 카드번호 masking 작업 진행.
        postProgress(organizationId, transactions)

        return ListTransactionsResponse().apply {
            this.dataBody = ListTransactionsResponseDataBody().apply {
                this.transactions = transactions
            }
        }
    }

    private fun getListTransactionsByDivision(
        executionContext: ExecutionContext,
        header: MutableMap<String, String?>,
        request: ListTransactionsRequest
    ): List<ExecutionResponse<ListTransactionsResponse>> {

        val searchDateList = getSearchDateList(executionContext, request)
        logger.With("searchDateList", searchDateList).Warn("")

        return runBlocking(executor.asCoroutineDispatcher()) {
            // 조회 시간 분할
            searchDateList.map {
                ListTransactionsRequest().apply {
                    dataHeader = ListTransactionsRequestDataHeader()
                    dataBody = ListTransactionsRequestDataBody().apply {
                        startAt = DateTimeUtil.localDateToString(it.startDate, "yyyyMMdd")
                        endAt = DateTimeUtil.localDateToString(it.endDate, "yyyyMMdd")
                        nextKey = ""
                    }
                }
            }
                .map {
                    async {
                        val executionResponse: ExecutionResponse<ListTransactionsResponse> =
                            collectExecutorService.execute(
                                executionContext,
                                Executions.valueOf(
                                    BusinessType.card,
                                    Organization.shinhancard,
                                    Transaction.cardTransaction
                                ),
                                ExecutionRequest.builder<ListTransactionsRequest>()
                                    .headers(header)
                                    .request(it)
                                    .build()
                            )

                        executionResponse
                    }
                }
        }.let {
            it.map { deferred ->
                deferred.getCompleted()
            }
        }
    }

    /**
     * 거래내역 Async 범위 일자 생성
     * @param 2020.01.01 ~ 2020.12.01
     * @return [ [2020.01.01~2020.03.31],[2020.04.01~2020.08.31],[2020.09.01~2020.12.31] ]
     */
    fun getSearchDateList(executionContext: ExecutionContext, request: ListTransactionsRequest): MutableList<DateTimeUtil.LocalDateRange> {

        return request.let { request ->
            validationService.validateOrThrows(request.dataBody)
        }
        ?.let { dataBody ->
            val startDate = DateTimeUtil.stringToLocalDate(dataBody.startAt, "yyyyMMdd")
            val endDate = DateTimeUtil.stringToLocalDate(dataBody.endAt, "yyyyMMdd")
            val division = organizationService.getOrganizationByOrganizationId(executionContext.organizationId).division

            DateTimeUtil.splitLocalDateRangeByMonth(startDate, endDate, division)
        }
        ?.toMutableList()
        ?: mutableListOf()
    }

    /**
     * 거래내역 조회 일자 생성
     * Execution Context 에 조회 날짜가 있는 경우 researchInterval 을 사용
     * 없는 경우 각 금융사별 MAX Month 로 생성
     * @return yyyyMMdd (year month day)
     */
    fun getStartAt(executionContext: CollectExecutionContext): String {
        val maxMonth = organizationService.getOrganizationByOrganizationId(executionContext.organizationId).maxMonth
        val defaultCheckStartTime = DateTimeUtil.kstNowLocalDateTime().minusMonths(maxMonth.toLong())

        val checkStartTime = userSyncStatusService.getUserSyncStatusLastCheckAt(
            executionContext.userId.toLong(), executionContext.organizationId, Transaction.cardTransaction.name
        )?.let { lastCheckAt ->
            val researchInterval = organizationService.getOrganizationByOrganizationId(executionContext.organizationId).researchInterval
            DateTimeUtil.epochMilliSecondToKSTLocalDateTime(lastCheckAt).minusDays(researchInterval.toLong())
        }?.takeIf {
            defaultCheckStartTime.isBefore(it)
        } ?: defaultCheckStartTime

        return DateTimeUtil.localDatetimeToString(checkStartTime, "yyyyMMdd")
    }

    fun postProgress(organizationId: String, transactions: MutableList<CardTransaction>) {
        when (organizationId) {
            shinhancardOrganizationId -> {
                transactions.map { transaction ->
                    transaction.cardNumber = CustomStringUtil.replaceNumberToMask(transaction.cardNumber)
                }
            }
            else -> {}
        }
    }
}
