package com.rainist.collectcard.cardtransactions

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
import com.rainist.collectcard.cardtransactions.validation.ListCardRequestValidator
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.common.db.repository.CardTransactionRepository
import com.rainist.collectcard.common.service.CardOrganization
import com.rainist.collectcard.common.service.HeaderService
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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@ExperimentalCoroutinesApi
@Service
class CardTransactionServiceImpl(
    val headerService: HeaderService,
    val collectExecutorService: CollectExecutorService,
    val listCardRequestValidator: ListCardRequestValidator,
    val validationService: ValidationService,
    val cardTransactionRepository: CardTransactionRepository,
    @Qualifier("async-thread") val executor: Executor
) : CardTransactionService {

    companion object : Log {
        const val DEFAULT_MAX_MONTH = 12L
        const val DEFAULT_DIVISION = 3
    }

    fun execute(
        header: MutableMap<String, String?>,
        request: ListTransactionsRequest,
        division: Int
    ): MutableList<CardTransaction> {

        val searchDateList = let {
            validationService.validateOrThrows(request.dataBody)
        }?.let { dataBody ->
            val startDate = DateTimeUtil.stringToLocalDate(dataBody.startAt, "yyyyMMdd")
            val endDate = DateTimeUtil.stringToLocalDate(dataBody.endAt, "yyyyMMdd")
            DateTimeUtil.splitLocalDateRangeByMonth(
                startDate, endDate, division
            )
        }?.toMutableList() ?: mutableListOf()

        var resultBody = runBlocking(executor.asCoroutineDispatcher()) {
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
                println("time is :${it.dataBody?.startAt} ~ ${it.dataBody?.endAt}")
                async {
                    val res: ExecutionResponse<ListTransactionsResponse> =
                        collectExecutorService.execute(
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
        }.sortedByDescending { cardTransaction -> cardTransaction.approvalDay + cardTransaction.approvalTime }.toMutableList()
    }

    @Transactional
    override fun listTransactions(banksaladUserId: String, organization: CardOrganization, fromMs: Long?): ListTransactionsResponse {
        val header = headerService.makeHeader(banksaladUserId, organization)

        val request = ListTransactionsRequest().apply {
            this.organizationObjectid = organization.organizationObjectId
            this.dataHeader = ListTransactionsRequestDataHeader()
            this.dataBody = ListTransactionsRequestDataBody().apply {
                this.startAt = let {
                    if (fromMs != null) {
                        val localdateTime = DateTimeUtil.epochMilliSecondToKSTLocalDateTime(fromMs)
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
                            DateTimeUtil.kstNowLocalDate().minusMonths(organization.maxMonth.toLong()),
                            "yyyyMMdd"
                        )
                    }
                }
            }
        }

        val response = execute(header, request, organization.division)

        // db insert
        response.forEach { cardTransaction ->
            var cardTransactionEntity = cardTransactionRepository.findByBanksaladUserIdAndAndCardCompanyIdAndCardCompanyCardIdAndApprovalNumberAndIssuedDate(
                banksaladUserId.toLong(),
                organization.organizationId,
                cardTransaction.cardCompanyCardId,
                cardTransaction.approvalNumber,
                DateTimeUtil.stringToLocalDateTime(
                    cardTransaction.approvalDay!!, "yyyyMMdd",
                    cardTransaction.approvalTime!!, "HHmmss"
                )
            )

            if (cardTransactionEntity == null) {
                cardTransactionRepository.save(
                    CardTransactionUtil.makeCardTransactionEntity(banksaladUserId.toLong(), organization.organizationId.toString(), cardTransaction)
                )
            }
        }

        // return
        return ListTransactionsResponse().apply {
            this.dataBody = ListTransactionsResponseDataBody().apply {
                this.transactions = response
            }
        }
    }
}
