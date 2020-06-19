package com.rainist.collectcard.grpc.handler

import com.github.rainist.idl.apis.v1.collectcard.CollectcardGrpc
import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import com.rainist.common.interceptor.StatsUnaryServerInterceptor
import io.grpc.stub.StreamObserver
import org.lognet.springboot.grpc.GRpcService

@GRpcService(interceptors = [StatsUnaryServerInterceptor::class])
class CollectcardGrpcService : CollectcardGrpc.CollectcardImplBase() {
    override fun healthCheck(request: CollectcardProto.HealthCheckRequest, responseObserver: StreamObserver<CollectcardProto.HealthCheckResponse>) {
        val resp = CollectcardProto.HealthCheckResponse.newBuilder().build()
        responseObserver.onNext(resp)
        responseObserver.onCompleted()
    }

    override fun listCards(request: CollectcardProto.ListCardsRequest, responseObserver: StreamObserver<CollectcardProto.ListCardsResponse>) {
        val resp = CollectcardProto.ListCardsResponse.newBuilder().build()
        responseObserver.onNext(resp)
        responseObserver.onCompleted()
    }

    override fun listCardTransactions(request: CollectcardProto.ListCardTransactionsRequest, responseObserver: StreamObserver<CollectcardProto.ListCardTransactionsResponse>) {
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
