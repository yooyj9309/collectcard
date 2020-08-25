package com.rainist.collectcard.cardtransactions

import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
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
import com.rainist.common.log.Log
import com.rainist.common.service.ValidationService
import com.rainist.common.util.DateTimeUtil
import java.time.LocalDate
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
    @Qualifier("async-thread") val executor: Executor
) : CardTransactionService {

    companion object : Log

    @Value("\${shinhancard.organizationId}")
    lateinit var shinhancardOrganizationId: String

    @Transactional
    override fun listTransactions(executionContext: CollectExecutionContext): ListTransactionsResponse {
        val now = DateTimeUtil.utcNowLocalDateTime()
        val banksaladUserId = executionContext.userId.toLong()

        /* request header */
        val header = headerService.makeHeader(executionContext.userId, executionContext.organizationId)

        /* request body */
        val request = ListTransactionsRequest().apply {
            this.dataHeader = ListTransactionsRequestDataHeader()
            this.dataBody = ListTransactionsRequestDataBody().apply {
                this.startAt = userSyncStatusService.getUserSyncStatusLastCheckAt(banksaladUserId, executionContext.organizationId, Transaction.cardTransaction.name)
                    ?.let {
                        val researchInterval = organizationService.getOrganizationByOrganizationId(executionContext.organizationId).researchInterval
                        DateTimeUtil.epochMilliSecondToKSTLocalDateTime(it).minusDays(researchInterval.toLong())
                    }
                    ?.let { localDateTime ->
                        DateTimeUtil.localDateToString(LocalDate.of(localDateTime.year, localDateTime.month, localDateTime.dayOfMonth), "yyyyMMdd")
                    }
                    ?: kotlin.run {
                        val maxMonth = organizationService.getOrganizationByOrganizationId(executionContext.organizationId).maxMonth
                        DateTimeUtil.localDateToString(DateTimeUtil.kstNowLocalDate().minusMonths(maxMonth.toLong()), "yyyyMMdd")
                    }
                this.endAt = DateTimeUtil.kstNowLocalDateString("yyyyMMdd")
            }
        }

        logger.With("banksaladUserId", executionContext.userId).With("startAt", request.dataBody?.startAt ?: "startAtNull").Warn("")

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

        if (executionResponseValidateService.validate(executionContext, executionResponses)) {
                userSyncStatusService.updateUserSyncStatus(
                banksaladUserId,
                executionContext.organizationId,
                Transaction.cardTransaction.name,
                DateTimeUtil.utcLocalDateTimeToEpochMilliSecond(now))
        }

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

        val searchDateList = let {
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
}
