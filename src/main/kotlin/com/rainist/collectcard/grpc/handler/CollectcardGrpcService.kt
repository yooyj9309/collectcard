package com.rainist.collectcard.grpc.handler

import com.github.rainist.idl.apis.v1.collectcard.CollectcardGrpc
import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.collectcard.card.CardServiceImpl
import com.rainist.collectcard.card.dto.ListCardsRequest
import com.rainist.collectcard.cardtransactions.CardTransactionServiceImpl
import com.rainist.common.interceptor.StatsUnaryServerInterceptor
import com.rainist.common.log.Log
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.lognet.springboot.grpc.GRpcService

@GRpcService(interceptors = [StatsUnaryServerInterceptor::class])
class CollectcardGrpcService(
    val cardService: CardServiceImpl,
    val cardTransactionService: CardTransactionServiceImpl
) : CollectcardGrpc.CollectcardImplBase() {

    companion object : Log

    override fun healthCheck(request: CollectcardProto.HealthCheckRequest, responseObserver: StreamObserver<CollectcardProto.HealthCheckResponse>) {
        val resp = CollectcardProto.HealthCheckResponse.newBuilder().build()
        responseObserver.onNext(resp)
        responseObserver.onCompleted()
    }

    override fun listCards(request: CollectcardProto.ListCardsRequest, responseObserver: StreamObserver<CollectcardProto.ListCardsResponse>) {
        kotlin.runCatching {
            cardService.listCards(request)
        }.onSuccess {
            responseObserver.onNext(it)
            responseObserver.onCompleted()
        }.onFailure {
            responseObserver.onError(it)
        }
    }

    @ExperimentalCoroutinesApi
    override fun listCardTransactions(request: CollectcardProto.ListCardTransactionsRequest, responseObserver: StreamObserver<CollectcardProto.ListCardTransactionsResponse>) {
        logger.info("listCardTransactions : {}", request.toString())

        kotlin.runCatching {
            cardTransactionService.listTransactions(request)
        }
            .onSuccess {
                // TODO 예상국 테스트 코드 작성후 주석 풀기
                /*responseObserver.onNext(it)
                responseObserver.onCompleted()*/
            }
            .onFailure {
                // TODO 예상국 exception  처리 코드 추가 하기
                // responseObserver.onError(it)
            }

        // TODO 테스트 코드 작성후 삭제
        val resp = CollectcardProto.ListCardTransactionsResponse.newBuilder().build()
        responseObserver.onNext(resp)
        responseObserver.onCompleted()
    }

    override fun listCardBills(request: CollectcardProto.ListCardBillsRequest, responseObserver: StreamObserver<CollectcardProto.ListCardBillsResponse>) {
        val resp = CollectcardProto.ListCardBillsResponse.newBuilder().build()
        responseObserver.onNext(resp)
        responseObserver.onCompleted()
    }

    override fun listCardLoans(request: CollectcardProto.ListCardLoansRequest, responseObserver: StreamObserver<CollectcardProto.ListCardLoansResponse>) {
        val resp = CollectcardProto.ListCardLoansResponse.newBuilder().build()
        responseObserver.onNext(resp)
        responseObserver.onCompleted()
    }

    override fun getCreditLimit(request: CollectcardProto.GetCreditLimitRequest, responseObserver: StreamObserver<CollectcardProto.GetCreditLimitResponse>) {
        val resp = CollectcardProto.GetCreditLimitResponse.newBuilder().build()
        responseObserver.onNext(resp)
        responseObserver.onCompleted()
    }
}
