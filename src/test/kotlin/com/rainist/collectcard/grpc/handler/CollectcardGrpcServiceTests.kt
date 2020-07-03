package com.rainist.collectcard.grpc.handler

import com.github.rainist.idl.apis.v1.collectcard.CollectcardProto
import io.grpc.internal.testing.StreamRecorder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class CollectcardGrpcServiceTests {

    @Autowired
    lateinit var service: CollectcardGrpcService

    @Test
    fun testHealthCheck() {
        val request = CollectcardProto.HealthCheckRequest.newBuilder().build()
        val responseObserver: StreamRecorder<CollectcardProto.HealthCheckResponse> = StreamRecorder.create()

        service.healthCheck(request, responseObserver)
        val results: List<CollectcardProto.HealthCheckResponse> = responseObserver.values
        Assertions.assertEquals(1, results.size)

        val response: CollectcardProto.HealthCheckResponse = results[0]
        Assertions.assertEquals(CollectcardProto.HealthCheckResponse.newBuilder().build(), response)
    }

//    @Test
//    fun testListCards() {
//        val request = CollectcardProto.ListCardsRequest.newBuilder().build()
//        val responseObserver: StreamRecorder<CollectcardProto.ListCardsResponse> = StreamRecorder.create()
//
//        service.listCards(request, responseObserver)
//        val results: List<CollectcardProto.ListCardsResponse> = responseObserver.values
//        Assertions.assertEquals(1, results.size)
//
//        val response: CollectcardProto.ListCardsResponse = results[0]
//        Assertions.assertEquals(CollectcardProto.ListCardsResponse.newBuilder().build(), response)
//    }

/*    @Test
    fun testlistCardTransactions() {
        val request = CollectcardProto.ListCardTransactionsRequest.newBuilder().build()
        val responseObserver: StreamRecorder<CollectcardProto.ListCardTransactionsResponse> = StreamRecorder.create()

        service.listCardTransactions(request, responseObserver)
        val results: List<CollectcardProto.ListCardTransactionsResponse> = responseObserver.values
        Assertions.assertEquals(1, results.size)

        val response: CollectcardProto.ListCardTransactionsResponse = results[0]
        Assertions.assertEquals(CollectcardProto.ListCardTransactionsResponse.newBuilder().build(), response)
    }*/

    @Test
    fun testListCardBills() {
        val request = CollectcardProto.ListCardBillsRequest.newBuilder().build()
        val responseObserver: StreamRecorder<CollectcardProto.ListCardBillsResponse> = StreamRecorder.create()

        service.listCardBills(request, responseObserver)
        val results: List<CollectcardProto.ListCardBillsResponse> = responseObserver.values
        Assertions.assertEquals(1, results.size)

        val response: CollectcardProto.ListCardBillsResponse = results[0]
        Assertions.assertEquals(CollectcardProto.ListCardBillsResponse.newBuilder().build(), response)
    }

/*    @Test
    fun testListCardLoans() {
        val request = CollectcardProto.ListCardLoansRequest.newBuilder().build()
        val responseObserver: StreamRecorder<CollectcardProto.ListCardLoansResponse> = StreamRecorder.create()

        service.listCardLoans(request, responseObserver)
        val results: List<CollectcardProto.ListCardLoansResponse> = responseObserver.values
        Assertions.assertEquals(1, results.size)

        val response: CollectcardProto.ListCardLoansResponse = results[0]
        Assertions.assertEquals(CollectcardProto.ListCardLoansResponse.newBuilder().build(), response)
    }*/

    @Test
    fun testGetCreditLimit() {
        val request = CollectcardProto.GetCreditLimitRequest.newBuilder().build()
        val responseObserver: StreamRecorder<CollectcardProto.GetCreditLimitResponse> = StreamRecorder.create()

        service.getCreditLimit(request, responseObserver)
        val results: List<CollectcardProto.GetCreditLimitResponse> = responseObserver.values
        Assertions.assertEquals(1, results.size)

        val response: CollectcardProto.GetCreditLimitResponse = results[0]
        Assertions.assertEquals(CollectcardProto.GetCreditLimitResponse.newBuilder().build(), response)
    }
}
