package com.rainist.collectcard.plcc.cardtransactions

import com.github.banksalad.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.collect.common.execution.Execution
import com.rainist.collect.common.execution.ExecutionRequest
import com.rainist.collect.common.execution.ExecutionResponse
import com.rainist.collect.executor.CollectExecutorService
import com.rainist.collectcard.common.collect.api.BusinessType
import com.rainist.collectcard.common.collect.api.Organization
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.collect.execution.Executions
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.service.EncodeService
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.service.OrganizationService
import com.rainist.collectcard.common.service.UuidService
import com.rainist.collectcard.plcc.cardtransactions.dto.PlccCardTransaction
import com.rainist.collectcard.plcc.cardtransactions.dto.PlccCardTransactionRequest
import com.rainist.collectcard.plcc.cardtransactions.dto.PlccCardTransactionRequestDataBody
import com.rainist.collectcard.plcc.cardtransactions.dto.PlccCardTransactionResponse
import com.rainist.collectcard.plcc.common.db.entity.PlccCardTransactionBenefitSummaryEntity
import com.rainist.collectcard.plcc.common.db.repository.PlccCardTransactionBenefitSummaryRepository
import com.rainist.collectcard.plcc.common.db.repository.PlccCardTransactionRepository
import com.rainist.common.service.ValidationService
import com.rainist.common.util.DateTimeUtil
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.LocalDateTime
import org.springframework.http.MediaType
import org.springframework.stereotype.Service

@Service
class PlccCardTransactionServiceImpl(
    val headerService: HeaderService,
    val organizationService: OrganizationService,
    val lottePlccExecutorService: CollectExecutorService,
    val validationService: ValidationService,
    val uuidService: UuidService,
    val plccCardTransactionRepository: PlccCardTransactionRepository,
    val plccCardTransactionConvertService: PlccCardTransactionConvertService,
    val encodeService: EncodeService,
    val plccCardTransactionBenefitSummaryRepository: PlccCardTransactionBenefitSummaryRepository

) : PlccCardTransactionService {

    override fun plccCardTransactions(
        executionContext: CollectExecutionContext,
        plccCardTransactionRequest: CollectcardProto.ListPlccRewardsTransactionsRequest
    ) {

        val executions = Executions.valueOf(
            BusinessType.plcc,
            Organization.lottecard,
            Transaction.plccCardTransaction
        )

        // req
        val request = makePlccTransactionRequest(plccCardTransactionRequest)

        // send
        val executionResponse: ExecutionResponse<PlccCardTransactionResponse> =
            plccTransactionExecute(executionContext, executions, request)

        // validation
        val transactions = plccTransactionValidate(executionResponse)

        decode(executionResponse)

        // DB Insert
        savePlccTransactions(executionContext, plccCardTransactionRequest, transactions)

        // DB Summary Insert
        savePlccTransactionSummary(
            request.request.dataBody?.inquiryYearMonth,
            executionContext.userId.toLong(),
            executionContext.organizationId,
            plccCardTransactionRequest.cardId.value,
            executionResponse
        )
    }

    fun savePlccTransactionSummary(yearMonth: String?, banksaladUserId: Long, organizationId: String, cardCompanyCardId: String, executionResponse: ExecutionResponse<PlccCardTransactionResponse>) {

        val prevSummary = plccCardTransactionBenefitSummaryRepository.findByApprovalYearMonthAndBanksaladUserIdAndCardCompanyIdAndCardCompanyCardId(
            yearMonth,
            banksaladUserId,
            organizationId,
            cardCompanyCardId
        )

        prevSummary?.let {
            it.totalBenefitCount = executionResponse.response.dataBody?.totalBenefitCount
            it.totalBenefitAmount = executionResponse.response.dataBody?.totalBenefitAmount
            it.totalSalesAmount = executionResponse.response.dataBody?.totalSalesAmount
            plccCardTransactionBenefitSummaryRepository.save(it)
        } ?: kotlin.run {
            // ????????????
            PlccCardTransactionBenefitSummaryEntity().apply {
                this.approvalYearMonth = yearMonth
                this.banksaladUserId = banksaladUserId
                this.cardCompanyId = organizationId
                this.cardCompanyCardId = cardCompanyCardId
                this.totalBenefitCount = executionResponse.response.dataBody?.totalBenefitCount
                this.totalBenefitAmount = executionResponse.response.dataBody?.totalBenefitAmount
                this.totalSalesAmount = executionResponse.response.dataBody?.totalSalesAmount
            }.run {
                plccCardTransactionBenefitSummaryRepository.save(this)
            }
        }
    }

    // TODO ????????? ??????????????? ??????
    fun decode(executionResponse: ExecutionResponse<PlccCardTransactionResponse>) {

        executionResponse.response.dataBody?.responseMessage?.let {
            executionResponse.response.dataBody?.responseMessage =
                encodeService.base64Decode(it, Charset.forName("MS949"))
        }

        executionResponse.response.dataBody?.transactionList?.forEach {
            it.serviceName = encodeService.base64Decode(it.serviceName, Charset.forName("MS949"))
            it.storeName = encodeService.base64Decode(it.storeName, Charset.forName("MS949"))
        }
    }

    // insert ( ??????, ??????????????? ?????? ???????????? ???????????? ????????????(??????????????? ????????? row ??????, ??????????????? ?????? ??????.), insert ??? ?????? )
    fun savePlccTransactions(
        executionContext: CollectExecutionContext,
        plccCardTransactionRequest: CollectcardProto.ListPlccRewardsTransactionsRequest,
        transactions: List<PlccCardTransaction>
    ) {
        transactions.forEach { plccCardTransaction ->
            val approvalDay = DateTimeUtil.stringToLocalDate(plccCardTransaction.approvalDay.toString(), "yyyyMMdd")
            val yearMonth = convertStringYearMonth(approvalDay)
            val now = DateTimeUtil.utcNowLocalDateTime()

            val prevEntity =
                plccCardTransactionRepository.findByApprovalYearMonthAndBanksaladUserIdAndCardCompanyIdAndCardCompanyCardIdAndApprovalNumberAndApprovalDayAndApprovalTimeAndCardTransactionType(
                    yearMonth.yearMonth,
                    executionContext.userId.toLong(),
                    executionContext.organizationId,
                    plccCardTransactionRequest.cardId.value,
                    plccCardTransaction.approvalNumber,
                    plccCardTransaction.approvalDay,
                    plccCardTransaction.approvalTime,
                    plccCardTransaction.cardTransactionType
                )

            if (prevEntity == null) {
                val entity = plccCardTransactionConvertService.plccCardTransactionToEntity(
                    plccCardTransaction,
                    executionContext.userId.toLong(),
                    executionContext.organizationId,
                    plccCardTransactionRequest.cardId.value,
                    yearMonth.yearMonth,
                    now
                )
                plccCardTransactionRepository.save(entity)
            } else {
                prevEntity.lastCheckAt = now
                plccCardTransactionRepository.save(prevEntity)
            }
        }
    }

    fun makePlccTransactionRequest(plccCardTransactionRequest: CollectcardProto.ListPlccRewardsTransactionsRequest): ExecutionRequest<PlccCardTransactionRequest> {
        return ExecutionRequest.builder<PlccCardTransactionRequest>()
            .headers(
                headerService.makeHeader(MediaType.APPLICATION_JSON_VALUE)
            )
            .request(
                PlccCardTransactionRequest().apply {
                    val requestYearMonth =
                        DateTimeUtil.epochMilliSecondToKSTLocalDateTime(plccCardTransactionRequest.requestMonthMs.value)
                    val stringYearMonth = convertStringYearMonth(requestYearMonth)

                    this.dataBody = PlccCardTransactionRequestDataBody().apply {
                        this.cardNumber = plccCardTransactionRequest.cardId.value
                        this.inquiryYearMonth = stringYearMonth.yearMonth
                    }
                }
            )
            .build()
    }

    fun plccTransactionExecute(
        executionContext: CollectExecutionContext,
        executions: Execution,
        request: ExecutionRequest<PlccCardTransactionRequest>
    ): ExecutionResponse<PlccCardTransactionResponse> {
        return lottePlccExecutorService.execute(executionContext, executions, request)
    }

    fun plccTransactionValidate(executionResponse: ExecutionResponse<PlccCardTransactionResponse>): List<PlccCardTransaction> {
        return executionResponse.response?.dataBody
            .let { plccCardTransactionResponseDataBody ->
                plccCardTransactionResponseDataBody?.transactionList
            }
            ?.mapNotNull { plccCardTransaction ->
                validationService.validateOrNull(plccCardTransaction)
            }
            ?.toList()
            ?: listOf()
    }
}

// TODO DateTimeUtil ??? ?????????

data class StringYearMonth(
    var year: String? = null,
    var month: String? = null,
    var yearMonth: String? = null
)

fun convertStringYearMonth(date: LocalDate): StringYearMonth {
    return StringYearMonth().apply {
        this.year = date.year.toString()
        this.month = if (date.monthValue.toString().length == 2) date.monthValue.toString() else "0${date.monthValue}"
        this.yearMonth = "${this.year}${this.month}"
    }
}

fun convertStringYearMonth(date: LocalDateTime): StringYearMonth {
    return StringYearMonth().apply {
        this.year = date.year.toString()
        // mm ??? ?????? 01,02,03,04 ??? ????????? ?????????, length() 2??? ?????? ?????? 0 padding
        this.month = if (date.monthValue.toString().length == 2) date.monthValue.toString() else "0${date.monthValue}"
        this.yearMonth = "${this.year}${this.month}"
    }
}
