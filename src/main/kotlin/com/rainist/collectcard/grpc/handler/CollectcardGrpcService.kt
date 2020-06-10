package com.rainist.collectcard.grpc.handler

import com.github.rainist.idl.apis.v1.collectcard.CollectcardGrpc
import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import io.grpc.stub.StreamObserver
import org.lognet.springboot.grpc.GRpcService

@GRpcService
class CollectcardGrpcService : CollectcardGrpc.CollectcardImplBase() {
    override fun healthCheck(request: CollectcardProto.HealthCheckRequest?, responseObserver: StreamObserver<CollectcardProto.HealthCheckResponse>?) {
        try {
            val resp = CollectcardProto.HealthCheckResponse.newBuilder().build()
            responseObserver?.onNext(resp)
            responseObserver?.onCompleted()
        } catch (e: Exception) {
            responseObserver?.onError(e)
        }
    }

    override fun listCards(request: CollectcardProto.ListCardsRequest?, responseObserver: StreamObserver<CollectcardProto.ListCardsResponse>?) {
        super.listCards(request, responseObserver)
    }

    override fun listCardTransactions(request: CollectcardProto.ListCardTransactionsRequest?, responseObserver: StreamObserver<CollectcardProto.ListCardTransactionsResponse>?) {
        super.listCardTransactions(request, responseObserver)
    }

    override fun listCardBills(request: CollectcardProto.ListCardBillsRequest?, responseObserver: StreamObserver<CollectcardProto.ListCardBillsResponse>?) {
        super.listCardBills(request, responseObserver)
    }

    override fun listCardLoans(request: CollectcardProto.ListCardLoansRequest?, responseObserver: StreamObserver<CollectcardProto.ListCardLoansResponse>?) {
        super.listCardLoans(request, responseObserver)
    }

    override fun getCreditLimit(request: CollectcardProto.GetCreditLimitRequest?, responseObserver: StreamObserver<CollectcardProto.GetCreditLimitResponse>?) {
        super.getCreditLimit(request, responseObserver)
    }
}
