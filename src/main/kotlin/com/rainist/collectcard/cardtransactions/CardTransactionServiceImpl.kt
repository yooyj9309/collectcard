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
import com.rainist.collectcard.common.util.SyncStatus
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
    @SyncStatus(transactionId = "cardTransactions")
    override fun listTransactions(executionContext: CollectExecutionContext, fromMs: Long?): ListTransactionsResponse {
        val now = DateTimeUtil.utcNowLocalDateTime()
        val banksaladUserId = executionContext.userId.toLong()

        /* request header */
        val header = headerService.makeHeader(executionContext.userId, executionContext.organizationId)

        /* request body */
        val request = ListTransactionsRequest().apply {
            this.dataHeader = ListTransactionsRequestDataHeader()
            this.dataBody = ListTransactionsRequestDataBody().apply {
                this.startAt = fromMs?.let {
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

        val logMap = mapOf(
            "banksaladUserId" to executionContext.userId,
            "startAt" to (request.dataBody?.startAt ?: "startAtNull")
        )
        logger.warn(logMap)

        val transactions = getListTransactionsByDivision(executionContext, header, request)

        // db insert
        transactions.forEach { cardTransaction ->

            if (shinhancardOrganizationId == executionContext.organizationId) {
                val code = cardTransaction.currencyCode.replace(" ", "").trim()
                cardTransaction.currencyCode = CardTransactionUtil.currencyCodeMap[code] ?: code
            }

            val approvalYearMonth = try {
                cardTransaction.approvalDay?.substring(0, 6) ?: ""
            } catch (e: ArrayIndexOutOfBoundsException) {
                ""
            }

            val cardTransactionEntity =
                cardTransactionRepository.findByApprovalYearMonthAndBanksaladUserIdAndAndCardCompanyIdAndCardCompanyCardIdAndApprovalNumberAndApprovalDayAndApprovalTime(
                    approvalYearMonth,
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

        /* check response result */
        // TODO : response validation
//        if (! executionResonseValidateService.validate(executionContext.executionRequestId, executionResponse)) {
//            userSyncStatusService.updateUserSyncStatus(
//                banksaladUserId,
//                executionContext.organizationId,
//                Transaction.cardTransaction.name,
//                DateTimeUtil.utcLocalDateTimeToEpochMilliSecond(now))
//        }

        // return
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
    ): MutableList<CardTransaction> {

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

        val logMap = mapOf(
            "searchDateList" to searchDateList
        )
        logger.warn(logMap)

        val resultBody = runBlocking(executor.asCoroutineDispatcher()) {
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
                logger.warn(
                    mapOf(
                        "startAt" to (it.dataBody?.startAt ?: "startAtNull"),
                        "endAt" to (it.dataBody?.endAt ?: "endAtNull"),
                        "banksaladUserId" to executionContext.userId
                    )
                )

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

        return resultBody.flatMap {
            it.response?.dataBody?.transactions?.toMutableList() ?: mutableListOf()
        }.filter {
            it.cardNumber?.length ?: 0 > 0
        }.mapNotNull {
            validationService.validateOrNull(it)
        }.toMutableList()
    }
}
