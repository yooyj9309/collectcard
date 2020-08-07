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
import com.rainist.collectcard.common.dto.SyncRequest
import com.rainist.collectcard.common.service.ApiLogService
import com.rainist.collectcard.common.service.HeaderService
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
    val apiLogService: ApiLogService,
    val headerService: HeaderService,
    val collectExecutorService: CollectExecutorService,
    val validationService: ValidationService,
    val cardTransactionRepository: CardTransactionRepository,
    @Qualifier("async-thread") val executor: Executor
) : CardTransactionService {

    @Value("\${shinhancard.organizationId}")
    lateinit var shinhancardOrganizationId: String

    companion object : Log {
        const val DEFAULT_MAX_MONTH = 12L
        const val DEFAULT_DIVISION_MONTH = 3
    }

    @Transactional
    @SyncStatus(transactionId = "cardTransactions")
    override fun listTransactions(syncRequest: SyncRequest, fromMs: Long?): ListTransactionsResponse {
        /* request header */
        val header = headerService.makeHeader(syncRequest.banksaladUserId.toString(), syncRequest.organizationId)

        /* request body */
        val request = ListTransactionsRequest().apply {
            this.dataHeader = ListTransactionsRequestDataHeader()
            this.dataBody = ListTransactionsRequestDataBody().apply {
                this.startAt = createStartAt(fromMs)
            }
        }

        /* Execution Context */
        val executionContext: ExecutionContext = CollectExecutionContext(
            organizationId = syncRequest.organizationId,
            userId = syncRequest.banksaladUserId.toString()
        )

        val transactions = getListTransactionsByDivision(executionContext, header, request)

        // db insert
        transactions.forEach { cardTransaction ->

            if (shinhancardOrganizationId == syncRequest.organizationId) {
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
                    syncRequest.banksaladUserId,
                    syncRequest.organizationId,
                    cardTransaction.cardCompanyCardId,
                    cardTransaction.approvalNumber,
                    cardTransaction.approvalDay,
                    cardTransaction.approvalTime
                )

            if (cardTransactionEntity == null) {
                cardTransactionRepository.save(
                    CardTransactionUtil.makeCardTransactionEntity(
                        syncRequest.banksaladUserId.toLong(),
                        syncRequest.organizationId,
                        cardTransaction
                    )
                )
            }
        }

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
        }?.let { dataBody ->
            val startDate = DateTimeUtil.stringToLocalDate(dataBody.startAt, "yyyyMMdd")
            val endDate = DateTimeUtil.stringToLocalDate(dataBody.endAt, "yyyyMMdd")
            DateTimeUtil.splitLocalDateRangeByMonth(
                startDate, endDate, DEFAULT_DIVISION_MONTH
            )
        }?.toMutableList() ?: mutableListOf()

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
                    async {
                        val res: ExecutionResponse<ListTransactionsResponse> =
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
                        res
                    }
                }
        }.let {
            it.map { deferred ->
                deferred.getCompleted()
            }
        }

        // TODO 박두상 validate를 추가한다면 여기서 한번에, 여러 API의 응답을 합쳐서 내려주기에 각각의 API에서 에러발생 가능성 있음, 해당 부분은 어떻게 처리할것인가.
        return resultBody.flatMap {
            it.response.dataBody?.transactions?.toMutableList() ?: mutableListOf()
        }.filter {
            it.cardNumber?.length ?: 0 > 0
        }.mapNotNull {
            validationService.validateOrNull(it)
        }.sortedByDescending { cardTransaction -> cardTransaction.approvalDay + cardTransaction.approvalTime }
            .toMutableList()
    }

    private fun createStartAt(startAt: Long?): String {
        return if (startAt != null) {
            val localdateTime = DateTimeUtil.epochMilliSecondToKSTLocalDateTime(startAt)
            DateTimeUtil.localDateToString(
                LocalDate.of(
                    localdateTime.year,
                    localdateTime.month,
                    localdateTime.dayOfMonth
                ),
                "yyyyMMdd"
            )
        } else {
            DateTimeUtil.localDateToString(
                DateTimeUtil.kstNowLocalDate().minusMonths(DEFAULT_MAX_MONTH),
                "yyyyMMdd"
            )
        }
    }
}
