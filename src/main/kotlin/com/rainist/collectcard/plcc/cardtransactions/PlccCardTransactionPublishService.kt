package com.rainist.collectcard.plcc.cardtransactions

import com.github.banksalad.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.plcc.common.db.repository.PlccCardTransactionRepository
import com.rainist.common.util.DateTimeUtil
import org.springframework.stereotype.Service

@Service
class PlccCardTransactionPublishService(
    val plccCardTransactionRepository: PlccCardTransactionRepository,
    val plccCardTransactionConvertService: PlccCardTransactionConvertService
) {

    fun plccCardTransactionPublish(executionContext: CollectExecutionContext, plccCardTransactionRequest: CollectcardProto.ListPlccRewardsTransactionsRequest): CollectcardProto.ListPlccRewardsTransactionsResponse {

        val organizationObjectId = executionContext.organizationId
        val banksaladUserId = executionContext.userId.toLong()
        val cardId = plccCardTransactionRequest.cardId.value
        val inquiryYearMonth = DateTimeUtil.epochMilliSecondToKSTLocalDateTime(plccCardTransactionRequest.requestMonthMs.value)
        val yearMonth = convertStringYearMonth(inquiryYearMonth)

        val transactions = plccCardTransactionRepository.findAllByApprovalYearMonthAndBanksaladUserIdAndCardCompanyIdAndCardCompanyCardId(yearMonth.yearMonth, banksaladUserId, organizationObjectId, cardId)
        val protoTransactions = transactions.map { plccCardTransactionConvertService.plccCardTransactionEntityToProto(it) }.toList()

        return CollectcardProto
            .ListPlccRewardsTransactionsResponse
            .newBuilder()
            .addAllRewardsTransactions(protoTransactions)
            .build()
    }
}
