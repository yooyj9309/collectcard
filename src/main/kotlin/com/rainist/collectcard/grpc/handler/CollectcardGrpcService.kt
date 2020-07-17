package com.rainist.collectcard.grpc.handler

import com.github.rainist.idl.apis.v1.collectcard.CollectcardGrpc
import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.collectcard.card.CardService
import com.rainist.collectcard.card.dto.toListCardsResponseProto
import com.rainist.collectcard.cardbills.CardBillServiceImpl
import com.rainist.collectcard.cardcreditlimit.CardCreditLimitService
import com.rainist.collectcard.cardloans.CardLoanService
import com.rainist.collectcard.cardloans.dto.toListCardLoansResponseProto
import com.rainist.collectcard.cardtransactions.CardTransactionServiceImpl
import com.rainist.collectcard.common.exception.CollectcardException
import com.rainist.collectcard.common.organization.Organizations
import com.rainist.common.interceptor.StatsUnaryServerInterceptor
import com.rainist.common.log.Log
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.lognet.springboot.grpc.GRpcService

@GRpcService(interceptors = [StatsUnaryServerInterceptor::class])
class CollectcardGrpcService(
    val cardService: CardService,
    val cardTransactionService: CardTransactionServiceImpl,
    val cardLoanService: CardLoanService,
    val cardBillService: CardBillServiceImpl,
    val cardCreditLimitService: CardCreditLimitService
) : CollectcardGrpc.CollectcardImplBase() {

    companion object : Log

    override fun healthCheck(request: CollectcardProto.HealthCheckRequest, responseObserver: StreamObserver<CollectcardProto.HealthCheckResponse>) {
        val resp = CollectcardProto.HealthCheckResponse.newBuilder().build()
        responseObserver.onNext(resp)
        responseObserver.onCompleted()
    }

    override fun listCards(request: CollectcardProto.ListCardsRequest, responseObserver: StreamObserver<CollectcardProto.ListCardsResponse>) {
        logger.debug("[사용자 카드 조회 시작 : {}]", request)

        val banksaladUserId = request.userId
        val organizationId: String = Organizations.valueOfCompanyId(request.companyId.value)?.name
            ?: throw CollectcardException("Fail to resolve cardCompanyId: ${request.companyId.value}")

        kotlin.runCatching {
            cardService.listCards(banksaladUserId, organizationId).toListCardsResponseProto()
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
    override fun listCardTransactions(request: CollectcardProto.ListCardTransactionsRequest, responseObserver: StreamObserver<CollectcardProto.ListCardTransactionsResponse>) {
        logger.debug("[사용자 카드 내역 조회 시작 : {}]", request)

        kotlin.runCatching {
            cardTransactionService.listTransactions(request)
        }
        .onSuccess {
            logger.info("[사용자 카드 내역 조회 결과 success]")
            for (transaction in it.dataList) {
                logger.info("[사용자 카드 내역 조회 결과 : {}]", transaction)
            }

            responseObserver.onNext(it)
            responseObserver.onCompleted()
        }
        .onFailure {
            logger.error("[사용자 카드 내역 조회 에러 : {}]", it.localizedMessage, it)
            // TODO 예상국 exception  처리 코드 추가 하기
            responseObserver.onError(it)
        }
    }

    override fun listCardBills(request: CollectcardProto.ListCardBillsRequest, responseObserver: StreamObserver<CollectcardProto.ListCardBillsResponse>) {
        kotlin.runCatching {
            cardBillService.listUserCardBills(request)
        }.onSuccess {
            responseObserver.onNext(it)
            responseObserver.onCompleted()
        }.onFailure {
            logger.error("[청구서 조회 에러 : {}]", it.localizedMessage, it)
            responseObserver.onError(it)
        }
    }

    override fun listCardLoans(request: CollectcardProto.ListCardLoansRequest, responseObserver: StreamObserver<CollectcardProto.ListCardLoansResponse>) {
        logger.debug("[사용자 대출 내역 조회 시작 : {}]", request)

        val banksaladUserId = request.userId
        val organizationId: String = Organizations.valueOfCompanyId(request.companyId.value).name
            ?: throw CollectcardException("Fail to resolve cardCompanyId: ${request.companyId.value}")

        kotlin.runCatching {
            cardLoanService.listCardLoans(banksaladUserId, organizationId).toListCardLoansResponseProto()
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

    override fun getCreditLimit(request: CollectcardProto.GetCreditLimitRequest, responseObserver: StreamObserver<CollectcardProto.GetCreditLimitResponse>) {
        logger.debug("[사용자 개인 한도 조회 시작 : {}]", request)

        kotlin.runCatching {
            cardCreditLimitService.cardCreditLimit(request)
        }.onSuccess {
            responseObserver.onNext(it)
            responseObserver.onCompleted()
        }.onFailure {
            logger.error("사용자 개인 한도 조회 에러 . {}", it.message)
            responseObserver.onError(it)
        }
    }
}
