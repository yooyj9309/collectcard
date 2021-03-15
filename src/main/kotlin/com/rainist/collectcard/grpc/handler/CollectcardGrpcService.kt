package com.rainist.collectcard.grpc.handler

import com.github.banksalad.idl.apis.v1.collectcard.CollectcardGrpc
import com.github.banksalad.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.collectcard.card.CardService
import com.rainist.collectcard.card.dto.toListCardsResponseProto
import com.rainist.collectcard.cardbills.CardBillService
import com.rainist.collectcard.cardbills.dto.toListCardBillsResponseProto
import com.rainist.collectcard.cardcreditlimit.CardCreditLimitService
import com.rainist.collectcard.cardcreditlimit.dto.toCreditLimitResponseProto
import com.rainist.collectcard.cardloans.CardLoanService
import com.rainist.collectcard.cardloans.dto.toListCardLoansResponseProto
import com.rainist.collectcard.cardtransactions.CardTransactionService
import com.rainist.collectcard.cardtransactions.dto.toListCardsTransactionResponseProto
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.dto.CollectShadowingResponse
import com.rainist.collectcard.common.dto.SingleCollectShadowingResponse
import com.rainist.collectcard.common.dto.toSyncStatusResponseProto
import com.rainist.collectcard.common.exception.CollectcardServiceExceptionHandler
import com.rainist.collectcard.common.exception.HealthCheckException
import com.rainist.collectcard.common.publish.banksalad.CardBillPublishService
import com.rainist.collectcard.common.publish.banksalad.CardLoanPublishService
import com.rainist.collectcard.common.publish.banksalad.CardPublishService
import com.rainist.collectcard.common.publish.banksalad.CardTransactionPublishService
import com.rainist.collectcard.common.publish.banksalad.CreditLimitPublishService
import com.rainist.collectcard.common.service.LocalDatetimeService
import com.rainist.collectcard.common.service.OrganizationService
import com.rainist.collectcard.common.service.UserSyncStatusService
import com.rainist.collectcard.common.service.UuidService
import com.rainist.collectcard.config.grpc.onException
import com.rainist.collectcard.plcc.cardrewards.PlccCardRewardsPublishService
import com.rainist.collectcard.plcc.cardrewards.PlccCardThresholdService
import com.rainist.collectcard.plcc.cardrewards.PlccCardTypeLimitService
import com.rainist.collectcard.plcc.cardrewards.dto.PlccRpcRequest
import com.rainist.collectcard.plcc.cardtransactions.PlccCardTransactionPublishService
import com.rainist.collectcard.plcc.cardtransactions.PlccCardTransactionService
import com.rainist.common.interceptor.StatsUnaryServerInterceptor
import com.rainist.common.log.Log
import io.grpc.stub.StreamObserver
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import org.lognet.springboot.grpc.GRpcService

@GRpcService(interceptors = [StatsUnaryServerInterceptor::class])
class CollectcardGrpcService(
    val organizationService: OrganizationService,
    val cardService: CardService,
    val cardTransactionService: CardTransactionService,
    val cardLoanService: CardLoanService,
    val cardBillService: CardBillService,
    val cardCreditLimitService: CardCreditLimitService,
    val userSyncStatusService: UserSyncStatusService,
    val uuidService: UuidService,
    val cardPublishService: CardPublishService,
    val cardTransactionPublishService: CardTransactionPublishService,
    val cardBillPublishService: CardBillPublishService,
    val cardLoanPublishService: CardLoanPublishService,
    val creditLimitPublishService: CreditLimitPublishService,
    val meterRegistry: MeterRegistry,
    val localDatetimeService: LocalDatetimeService,
    val plccCardTransactionService: PlccCardTransactionService,
    val plccCardTransactionPublishService: PlccCardTransactionPublishService,
    val plccCardThresholdService: PlccCardThresholdService,
    val plccCardRewardsPublishService: PlccCardRewardsPublishService,
    val plccCardTypeLimitService: PlccCardTypeLimitService
) : CollectcardGrpc.CollectcardImplBase() {

    companion object : Log

    override fun healthCheck(
        request: CollectcardProto.HealthCheckRequest,
        responseObserver: StreamObserver<CollectcardProto.HealthCheckResponse>
    ) {
        kotlin.runCatching {
            CollectcardProto.HealthCheckResponse.newBuilder().build()
        }.onSuccess {
            responseObserver.onNext(it)
            responseObserver.onCompleted()
        }.onFailure {
            val ex = HealthCheckException()
            responseObserver.onException(ex) // TODO it 으로 바꾸기
        }
    }

    override fun listCards(
        request: CollectcardProto.ListCardsRequest,
        responseObserver: StreamObserver<CollectcardProto.ListCardsResponse>
    ) {
        /* Execution Context */
        // 괴랄하게 LocalDatetime을 가져오는 이유는 TestCode에서 mocking문제로 인하여 다음과같이 가져오도록 수정하였습니다.
        val now = localDatetimeService.generateNowLocalDatetime().now

        val executionContext = CollectExecutionContext(
            executionRequestId = uuidService.generateExecutionRequestId(),
            organizationId = organizationService.getOrganizationByObjectId(request.companyId.value).organizationId
                ?: "",
            userId = request.userId
        )

        kotlin.runCatching {
            val listCardResponse = cardService.listCards(executionContext, now)
            GlobalScope.launch {
                val res: CollectShadowingResponse = cardPublishService.shadowing(
                    executionContext.userId.toLong(),
                    executionContext.organizationId,
                    now,
                    executionContext.executionRequestId,
                    listCardResponse
                )
                shadowingLogging(res)
            }

            listCardResponse.toListCardsResponseProto()
        }.onSuccess {
            responseObserver.onNext(it)
            responseObserver.onCompleted()
        }.onFailure {
            CollectcardServiceExceptionHandler.handle(executionContext, "listCards", "사용자카드조회", it)
            responseObserver.onError(it)
        }
    }

    @ExperimentalCoroutinesApi
    override fun listCardTransactions(
        request: CollectcardProto.ListCardTransactionsRequest,
        responseObserver: StreamObserver<CollectcardProto.ListCardTransactionsResponse>
    ) {
        val now = localDatetimeService.generateNowLocalDatetime().now
        /* Execution Context */
        val executionContext: CollectExecutionContext = CollectExecutionContext(
            executionRequestId = uuidService.generateExecutionRequestId(),
            organizationId = organizationService.getOrganizationByObjectId(request.companyId.value)?.organizationId
                ?: "",
            userId = request.userId
        )

        kotlin.runCatching {
            logger
                .With("banksaladUserId", request.userId)
                .With("fromMs", request.takeIf { request.hasFromMs() }?.fromMs?.value ?: "fromMsNull")
                .Warn("")

            val listTransactionResponse = cardTransactionService.listTransactions(executionContext, now)

            GlobalScope.launch {
                val res: CollectShadowingResponse = cardTransactionPublishService.shadowing(
                    executionContext.userId.toLong(),
                    executionContext.organizationId,
                    now,
                    executionContext.executionRequestId,
                    listTransactionResponse
                )
                shadowingLogging(res)
            }

            listTransactionResponse.toListCardsTransactionResponseProto()
        }.onSuccess {
            responseObserver.onNext(it)
            responseObserver.onCompleted()
        }.onFailure {
            CollectcardServiceExceptionHandler.handle(executionContext, "listCardTransactions", "사용자카드내역조회", it)
            responseObserver.onException(it)
        }
    }

    override fun listCardBills(
        request: CollectcardProto.ListCardBillsRequest,
        responseObserver: StreamObserver<CollectcardProto.ListCardBillsResponse>
    ) {
        val now = localDatetimeService.generateNowLocalDatetime().now
        /* Execution Context */
        val executionContext: CollectExecutionContext = CollectExecutionContext(
            executionRequestId = uuidService.generateExecutionRequestId(),
            organizationId = organizationService.getOrganizationByObjectId(request.companyId.value)?.organizationId
                ?: "",
            userId = request.userId
        )

        kotlin.runCatching {
            val listCardBillsResponse = cardBillService.listUserCardBills(executionContext, now)

            GlobalScope.launch {
                val res: CollectShadowingResponse = cardBillPublishService.shadowing(
                    executionContext.userId.toLong(),
                    executionContext.organizationId,
                    now,
                    executionContext.executionRequestId,
                    listCardBillsResponse
                )
                shadowingLogging(res)
            }

            listCardBillsResponse.toListCardBillsResponseProto()
        }.onSuccess {
            responseObserver.onNext(it)
            responseObserver.onCompleted()
        }.onFailure {
//            logger.error("[사용자 청구서 조회 에러 : {}]", it.localizedMessage, it)
            CollectcardServiceExceptionHandler.handle(executionContext, "listCardBills", "사용자청구서조회", it)
            responseObserver.onError(it)
        }
    }

    override fun listCardLoans(
        request: CollectcardProto.ListCardLoansRequest,
        responseObserver: StreamObserver<CollectcardProto.ListCardLoansResponse>
    ) {
        val now = localDatetimeService.generateNowLocalDatetime().now

        /* Execution Context */
        val executionContext: CollectExecutionContext = CollectExecutionContext(
            executionRequestId = uuidService.generateExecutionRequestId(),
            organizationId = organizationService.getOrganizationByObjectId(request.companyId.value)?.organizationId
                ?: "",
            userId = request.userId
        )

        kotlin.runCatching {
            val listLoanResponse = cardLoanService.listCardLoans(executionContext, now)

            GlobalScope.launch {
                val res: CollectShadowingResponse = cardLoanPublishService.shadowing(
                    executionContext.userId.toLong(),
                    executionContext.organizationId,
                    now,
                    executionContext.executionRequestId,
                    listLoanResponse
                )
                shadowingLogging(res)
            }
            listLoanResponse.toListCardLoansResponseProto()
        }.onSuccess {
            responseObserver.onNext(it)
            responseObserver.onCompleted()
        }.onFailure {
//                logger.error("[사용자 대출 내역 조회 에러 : {}]", it.localizedMessage, it)
            CollectcardServiceExceptionHandler.handle(executionContext, "listCardLoans", "사용자대출내역조회", it)
            // TODO 예상국 exception  처리 코드 추가 하기
            responseObserver.onError(it)
        }
    }

    override fun getCreditLimit(
        request: CollectcardProto.GetCreditLimitRequest,
        responseObserver: StreamObserver<CollectcardProto.GetCreditLimitResponse>
    ) {
        val now = localDatetimeService.generateNowLocalDatetime().now

        /* Execution Context */
        val executionContext: CollectExecutionContext = CollectExecutionContext(
            executionRequestId = uuidService.generateExecutionRequestId(),
            organizationId = organizationService.getOrganizationByObjectId(request.companyId.value)?.organizationId
                ?: "",
            userId = request.userId
        )

        kotlin.runCatching {
            val cardCreditLimitResponse = cardCreditLimitService.cardCreditLimit(executionContext, now)

            GlobalScope.launch {
                val res: SingleCollectShadowingResponse = creditLimitPublishService.shadowing(
                    executionContext.userId.toLong(),
                    executionContext.organizationId,
                    now,
                    executionContext.executionRequestId,
                    cardCreditLimitResponse
                )
                singleShadowingLogging(res)
            }

            cardCreditLimitResponse.toCreditLimitResponseProto()
        }.onSuccess {
            responseObserver.onNext(it)
            responseObserver.onCompleted()
        }.onFailure {
//            logger.error("사용자 개인 한도 조회 에러 . {}", it.message)
            CollectcardServiceExceptionHandler.handle(executionContext, "getCreditLimit", "사용자개인한도조회", it)
            responseObserver.onError(it)
        }
    }

    override fun getSyncStatus(
        request: CollectcardProto.GetSyncStatusRequest,
        responseObserver: StreamObserver<CollectcardProto.GetSyncStatusResponse>
    ) {
        val executionContext = if (StringUtils.isEmpty(request.companyId.value)) {
            CollectExecutionContext(
                executionRequestId = uuidService.generateExecutionRequestId(),
                organizationId = "",
                userId = request.userId
            )
        } else {
            CollectExecutionContext(
                executionRequestId = uuidService.generateExecutionRequestId(),
                organizationId = organizationService.getOrganizationByObjectId(request.companyId.value).organizationId
                    ?: "",
                userId = request.userId
            )
        }

        kotlin.runCatching {
            userSyncStatusService.getUserSyncStatus(executionContext).toSyncStatusResponseProto()
        }.onSuccess {
            responseObserver.onNext(it)
            responseObserver.onCompleted()
        }.onFailure {
            CollectcardServiceExceptionHandler.handle(executionContext, "getSyncStatus", "유저Sync시간조회", it)
            responseObserver.onError(it)
        }
    }

    override fun deleteSyncStatus(
        request: CollectcardProto.DeleteSyncStatusRequest,
        responseObserver: StreamObserver<CollectcardProto.DeleteSyncStatusResponse>
    ) {
        val executionContext = CollectExecutionContext(
            executionRequestId = uuidService.generateExecutionRequestId(),
            organizationId = organizationService.getOrganizationByObjectId(request.companyId.value)?.organizationId
                ?: "",
            userId = request.userId
        )

        kotlin.runCatching {
            userSyncStatusService.updateDeleteFlagByUserIdAndCompanyId(executionContext)
        }.onSuccess {
            responseObserver.onNext(CollectcardProto.DeleteSyncStatusResponse.newBuilder().build())
            responseObserver.onCompleted()
        }.onFailure {
            CollectcardServiceExceptionHandler.handle(executionContext, "deleteSyncStatus", "유저카드연동해제", it)
            responseObserver.onError(it)
        }
    }

    override fun deleteAllSyncStatus(
        request: CollectcardProto.DeleteAllSyncStatusRequest,
        responseObserver: StreamObserver<CollectcardProto.DeleteAllSyncStatusResponse>
    ) {
        kotlin.runCatching {
            userSyncStatusService.updateDeleteFlagByUserId(request.userId.toLong())
        }.onSuccess {
            responseObserver.onNext(CollectcardProto.DeleteAllSyncStatusResponse.newBuilder().build())
            responseObserver.onCompleted()
        }.onFailure {
            // CollectcardServiceExceptionHandler.handle(executionContext, "getSyncStatus", "유저데이터초기화", it)
            responseObserver.onError(it)
        }
    }

    override fun listPlccRewardsTransactions(
        request: CollectcardProto.ListPlccRewardsTransactionsRequest,
        responseObserver: StreamObserver<CollectcardProto.ListPlccRewardsTransactionsResponse>
    ) {

        val executionContext = CollectExecutionContext(
            executionRequestId = uuidService.generateExecutionRequestId(),
            organizationId = organizationService.getOrganizationByObjectId(request.companyId.value)?.organizationId
                ?: "",
            userId = request.userId
        )

        // TODO Log 삭제
        logger.Warn("PLCC listPlccRewardsTransactions context : {}", executionContext)

        kotlin.runCatching {
            plccCardTransactionService.plccCardTransactions(executionContext, request)
            plccCardTransactionPublishService.plccCardTransactionPublish(executionContext, request)
        }.onSuccess {
            responseObserver.onNext(it)
            responseObserver.onCompleted()
        }.onFailure {
            CollectcardServiceExceptionHandler.handle(
                executionContext,
                "listPlccRewardsTransactions",
                "PLCC 혜택 거래적용내역 오류",
                it
            )
            responseObserver.onError(it)
        }
    }

    override fun getPlccRewardsThreshold(
        request: CollectcardProto.GetPlccRewardsThresholdRequest,
        responseObserver: StreamObserver<CollectcardProto.GetPlccRewardsThresholdResponse>
    ) {

        // TODO : 삭제
        logger.info("request = {}", request)

        /* ExecutionContext */
        val executionContext = CollectExecutionContext(
            executionRequestId = uuidService.generateExecutionRequestId(),
            organizationId = organizationService.getOrganizationByObjectId(request.companyId.value)?.organizationId
                ?: "",
            userId = request.userId
        )

        /** getPlccCardRewards는 하나의 api로 요청하지만 rpc는 2개로 나눠져서
         *  매개변수의 타입을 rpc request 하나로 특정할 수 없다.
         *  공통적으로 사용하는 cardId, requestMonthMs를 담은 plccRpcRequest를 생성해 넘긴다.
         */
        val plccRpcRequest = PlccRpcRequest(request.cardId.value, request.requestMonthMs.value)

        kotlin.runCatching {
            plccCardThresholdService.getPlccCardThreshold(executionContext, plccRpcRequest)
            plccCardRewardsPublishService.rewardsThresholdPublish(executionContext, plccRpcRequest)
        }.onSuccess {
            responseObserver.onNext(it)
            responseObserver.onCompleted()
        }.onFailure {
            CollectcardServiceExceptionHandler.handle(executionContext, "getPlccRewardsThreshold", "혜택별 실적조회", it)
            responseObserver.onError(it)
        }
    }

    override fun listPlccRewardsTypeLimit(
        request: CollectcardProto.ListPlccRewardsTypeLimitRequest,
        responseObserver: StreamObserver<CollectcardProto.ListPlccRewardsTypeLimitResponse>
    ) {

        // TODO : 삭제
        logger.info("request = {}", request)

        /* ExecutionContext */
        val executionContext = CollectExecutionContext(
            executionRequestId = uuidService.generateExecutionRequestId(),
            organizationId = organizationService.getOrganizationByObjectId(request.companyId.value)?.organizationId
                ?: "",
            userId = request.userId
        )

        val plccRpcRequest = PlccRpcRequest(request.cardId.value, request.requestMonthMs.value)

        kotlin.runCatching {
            plccCardTypeLimitService.listPlccCardTypeLimit(executionContext, plccRpcRequest)
            plccCardRewardsPublishService.rewardsTypeLimitPublish(executionContext, plccRpcRequest)
        }.onSuccess {
            responseObserver.onNext(it)
            responseObserver.onCompleted()
        }.onFailure {
            CollectcardServiceExceptionHandler.handle(executionContext, "listPlccRewardsTypeLimit", "타입별 한도 조회", it)
            responseObserver.onError(it)
        }
    }

    private fun shadowingLogging(res: CollectShadowingResponse) {
        logger.With("shadowing_target", res.executionName)
            .With("banksalad_user_id", res.banksaladUserId)
            .With("organization_id", res.organizationId)
            .With("last_check_at", res.lastCheckAt)
            .With("execution_request_id", res.executionRequestId)
            .With("is_diff", res.isDiff)
            .Info()

        val tags = Tags.of("execution_name", res.executionName).and("is_diff", res.isDiff.toString())
        meterRegistry.counter("shadowing.all", tags).increment()
    }

    private fun singleShadowingLogging(res: SingleCollectShadowingResponse) {
        logger.With("shadowing_target", res.executionName)
            .With("banksalad_user_id", res.banksaladUserId)
            .With("organization_id", res.organizationId)
            .With("last_check_at", res.lastCheckAt)
            .With("execution_request_id", res.executionRequestId)
            .With("is_diff", res.isDiff)
            .Info()

        val tags = Tags.of("execution_name", res.executionName).and("is_diff", res.isDiff.toString())
        meterRegistry.counter("shadowing.all", tags).increment()
    }
}
