package com.rainist.collectcard.grpc.handler

import com.github.rainist.idl.apis.v1.collectcard.CollectcardGrpc
import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
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
import com.rainist.collectcard.common.dto.SyncRequest
import com.rainist.collectcard.common.exception.HealthCheckException
import com.rainist.collectcard.common.service.OrganizationService
import com.rainist.collectcard.config.onException
import com.rainist.common.interceptor.StatsUnaryServerInterceptor
import com.rainist.common.log.Log
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.lognet.springboot.grpc.GRpcService

@GRpcService(interceptors = [StatsUnaryServerInterceptor::class])
class CollectcardGrpcService(
    val organizationService: OrganizationService,
    val cardService: CardService,
    val cardTransactionService: CardTransactionService,
    val cardLoanService: CardLoanService,
    val cardBillService: CardBillService,
    val cardCreditLimitService: CardCreditLimitService
) : CollectcardGrpc.CollectcardImplBase() {

    companion object : Log

    override fun healthCheck(
        request: CollectcardProto.HealthCheckRequest,
        responseObserver: StreamObserver<CollectcardProto.HealthCheckResponse>
    ) {
        kotlin.runCatching {
            CollectcardProto.HealthCheckResponse.newBuilder().build()
        }
            .onSuccess {
                responseObserver.onNext(it)
                responseObserver.onCompleted()
            }
            .onFailure {
                val ex = HealthCheckException()
                responseObserver.onException(ex) // TODO it 으로 바꾸기
            }
    }

    override fun listCards(
        request: CollectcardProto.ListCardsRequest,
        responseObserver: StreamObserver<CollectcardProto.ListCardsResponse>
    ) {
        logger.debug("[사용자 카드 조회 시작 : {}]", request)

        // TODO : userId validation, organizationId validation (userId는 Long 인지 여부, orngaizationId 는 변환후 null 여부
        val syncRequest = SyncRequest(
            request.userId.toLong(),
            organizationService.getOrganizationByObjectId(request.companyId.value)?.organizationId ?: ""
        )

        kotlin.runCatching {
            cardService.listCards(syncRequest).toListCardsResponseProto()
        }.onSuccess {
            logger.info("[사용자 카드 조회 결과 success]")

            for (card in it.dataList) {
                logger.info("[사용자 카드 조회 결과 : {}]", card)
            }

            responseObserver.onNext(it)
            responseObserver.onCompleted()
        }.onFailure {
            logger.error("[사용자 카드 조회 에러 : {}]", it.localizedMessage, it)
            responseObserver.onError(it)
        }
    }

    @ExperimentalCoroutinesApi
    override fun listCardTransactions(
        request: CollectcardProto.ListCardTransactionsRequest,
        responseObserver: StreamObserver<CollectcardProto.ListCardTransactionsResponse>
    ) {
        logger.debug("[사용자 카드 내역 조회 시작 : {}]", request)

        val syncRequest = SyncRequest(
            request.userId.toLong(),
            organizationService.getOrganizationByObjectId(request.companyId.value).organizationId ?: ""
        )
        kotlin.runCatching {
            cardTransactionService.listTransactions(
                syncRequest,
                request.takeIf { request.hasFromMs() }?.fromMs?.value
            ).toListCardsTransactionResponseProto()
            // cardTransactionService.listTransactions(request)
        }.onSuccess {
            logger.info("[사용자 카드 내역 조회 결과 success]")
            for (transaction in it.dataList) {
                logger.info("[사용자 카드 내역 조회 결과 : {}]", transaction)
            }

            responseObserver.onNext(it)
            responseObserver.onCompleted()
        }.onFailure {
            logger.error("[사용자 카드 내역 조회 에러 : {}]", it.localizedMessage, it)
            // TODO 예상국 exception  처리 코드 추가 하기
            responseObserver.onException(it)
        }
    }

    override fun listCardBills(
        request: CollectcardProto.ListCardBillsRequest,
        responseObserver: StreamObserver<CollectcardProto.ListCardBillsResponse>
    ) {
        logger.debug("[사용자 청구서 조회 시작 : {}]", request)

        val syncRequest = SyncRequest(
            request.userId.toLong(),
            organizationService.getOrganizationByObjectId(request.companyId.value).organizationId ?: ""
        )

        kotlin.runCatching {
            cardBillService.listUserCardBills(
                syncRequest,
                request.takeIf { request.hasFromMs() }?.fromMs?.value
            ).toListCardBillsResponseProto()
        }.onSuccess {
            logger.info("[사용자 청구서 조회 결과 success]")

            for (card in it.dataList) {
                logger.info("[사용자 청구서 조회 결과 : {}]", card)
            }

            responseObserver.onNext(it)
            responseObserver.onCompleted()
        }.onFailure {
            logger.error("[사용자 청구서 조회 에러 : {}]", it.localizedMessage, it)
            responseObserver.onError(it)
        }
    }

    override fun listCardLoans(
        request: CollectcardProto.ListCardLoansRequest,
        responseObserver: StreamObserver<CollectcardProto.ListCardLoansResponse>
    ) {
        logger.debug("[사용자 대출 내역 조회 시작 : {}]", request)

        val syncRequest = SyncRequest(
            request.userId.toLong(),
            organizationService.getOrganizationByObjectId(request.companyId.value).organizationId ?: ""
        )

        kotlin.runCatching {
            cardLoanService.listCardLoans(syncRequest).toListCardLoansResponseProto()
        }
            .onSuccess {
                logger.info("[사용자 대출내역 조회 결과 success]")
                it.let {
                    for (loan in it.dataList) {
                        logger.info("[사용자 대출내역 조회 결과 : {}]", loan)
                    }
                }
                responseObserver.onNext(it)
                responseObserver.onCompleted()
            }
            .onFailure {
                logger.error("[사용자 대출 내역 조회 에러 : {}]", it.localizedMessage, it)
                // TODO 예상국 exception  처리 코드 추가 하기
                responseObserver.onError(it)
            }
    }

    override fun getCreditLimit(
        request: CollectcardProto.GetCreditLimitRequest,
        responseObserver: StreamObserver<CollectcardProto.GetCreditLimitResponse>
    ) {
        val syncRequest = SyncRequest(
            request.userId.toLong(),
            organizationService.getOrganizationByObjectId(request.companyId.value).organizationId ?: ""
        )

        logger.debug("[사용자 개인 한도 조회 시작 : {}]", request)

        kotlin.runCatching {
            cardCreditLimitService.cardCreditLimit(syncRequest).toCreditLimitResponseProto()
        }.onSuccess {
            responseObserver.onNext(it)
            responseObserver.onCompleted()
        }.onFailure {
            logger.error("사용자 개인 한도 조회 에러 . {}", it.message)
            responseObserver.onError(it)
        }
    }
}
