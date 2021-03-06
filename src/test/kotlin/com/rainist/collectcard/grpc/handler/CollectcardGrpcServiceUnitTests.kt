package com.rainist.collectcard.grpc.handler

import com.github.banksalad.idl.apis.v1.collectcard.CollectcardProto
import com.google.protobuf.StringValue
import com.rainist.collectcard.card.CardService
import com.rainist.collectcard.card.dto.Card
import com.rainist.collectcard.card.dto.ListCardsResponse
import com.rainist.collectcard.card.dto.ListCardsResponseDataBody
import com.rainist.collectcard.card.dto.ListCardsResponseDataHeader
import com.rainist.collectcard.card.dto.toListCardsResponseProto
import com.rainist.collectcard.cardbills.CardBillService
import com.rainist.collectcard.cardbills.dto.BillCardType
import com.rainist.collectcard.cardbills.dto.CardBill
import com.rainist.collectcard.cardbills.dto.ListCardBillsResponse
import com.rainist.collectcard.cardbills.dto.ListCardBillsResponseDataBody
import com.rainist.collectcard.cardbills.dto.ListCardBillsResponseDataHeader
import com.rainist.collectcard.cardbills.dto.toListCardBillsResponseProto
import com.rainist.collectcard.cardcreditlimit.CardCreditLimitService
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimit
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitResponse
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitResponseDataBody
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitResponseDataHeader
import com.rainist.collectcard.cardcreditlimit.dto.Limit
import com.rainist.collectcard.cardcreditlimit.dto.toCreditLimitResponseProto
import com.rainist.collectcard.cardloans.CardLoanService
import com.rainist.collectcard.cardloans.dto.ListLoansResponse
import com.rainist.collectcard.cardloans.dto.ListLoansResponseDataBody
import com.rainist.collectcard.cardloans.dto.ListLoansResponseDataHeader
import com.rainist.collectcard.cardloans.dto.Loan
import com.rainist.collectcard.cardloans.dto.LoanResponseDataHeader
import com.rainist.collectcard.cardloans.dto.toListCardLoansResponseProto
import com.rainist.collectcard.cardtransactions.CardTransactionService
import com.rainist.collectcard.cardtransactions.dto.CardTransaction
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponseDataBody
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponseDataHeader
import com.rainist.collectcard.cardtransactions.dto.toListCardsTransactionResponseProto
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.dto.NowUtcLocalDatetime
import com.rainist.collectcard.common.dto.SyncStatusResponse
import com.rainist.collectcard.common.dto.UserSyncStatusResponse
import com.rainist.collectcard.common.dto.toSyncStatusResponseProto
import com.rainist.collectcard.common.enums.CardOwnerType
import com.rainist.collectcard.common.enums.CardTransactionType
import com.rainist.collectcard.common.enums.CardType
import com.rainist.collectcard.common.enums.ResultCode
import com.rainist.collectcard.common.exception.CollectcardServiceExceptionHandler
import com.rainist.collectcard.common.meters.CollectMeterRegistry
import com.rainist.collectcard.common.publish.banksalad.CardBillPublishService
import com.rainist.collectcard.common.publish.banksalad.CardLoanPublishService
import com.rainist.collectcard.common.publish.banksalad.CardPublishService
import com.rainist.collectcard.common.publish.banksalad.CardTransactionPublishService
import com.rainist.collectcard.common.publish.banksalad.CreditLimitPublishService
import com.rainist.collectcard.common.service.CardOrganization
import com.rainist.collectcard.common.service.LocalDatetimeService
import com.rainist.collectcard.common.service.OrganizationService
import com.rainist.collectcard.common.service.UserSyncStatusService
import com.rainist.collectcard.common.service.UuidService
import com.rainist.collectcard.grpc.client.ConnectClientServiceTest
import com.rainist.collectcard.plcc.cardrewards.PlccCardRewardsPublishService
import com.rainist.collectcard.plcc.cardrewards.PlccCardRewardsService
import com.rainist.collectcard.plcc.cardrewards.PlccCardThresholdService
import com.rainist.collectcard.plcc.cardtransactions.PlccCardTransactionPublishService
import com.rainist.collectcard.plcc.cardtransactions.PlccCardTransactionService
import com.rainist.common.util.DateTimeUtil
import io.grpc.internal.testing.StreamRecorder
import io.micrometer.core.instrument.MeterRegistry
import java.math.BigDecimal
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.doNothing
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(value = [SpringExtension::class])
@ContextConfiguration(classes = [CollectcardGrpcService::class, CollectcardServiceExceptionHandler::class])
@Import(ConnectClientServiceTest.MockConfiguration::class)
internal class CollectcardGrpcServiceUnitTests {

    @Autowired
    lateinit var collectcardGrpcService: CollectcardGrpcService

    @MockBean
    lateinit var organizationService: OrganizationService

    @MockBean
    lateinit var cardService: CardService

    @MockBean
    lateinit var cardTransactionService: CardTransactionService

    @MockBean
    lateinit var cardLoanService: CardLoanService

    @MockBean
    lateinit var cardBillService: CardBillService

    @MockBean
    lateinit var cardCreditLimitService: CardCreditLimitService

    @MockBean
    lateinit var userSyncStatusService: UserSyncStatusService

    @MockBean
    lateinit var uuidService: UuidService

    @MockBean
    lateinit var localDatetimeService: LocalDatetimeService

    @MockBean
    lateinit var meterRegistry: MeterRegistry

    @MockBean
    lateinit var collectMeterRegistry: CollectMeterRegistry

    @MockBean
    lateinit var cardPublishService: CardPublishService

    @MockBean
    lateinit var creditLimitPublishService: CreditLimitPublishService

    @MockBean
    lateinit var cardTransactionPublishService: CardTransactionPublishService

    @MockBean
    lateinit var cardBillPublishService: CardBillPublishService

    @MockBean
    lateinit var cardLoanPublishService: CardLoanPublishService

    @MockBean
    lateinit var plccCardThresholdService: PlccCardThresholdService

    @MockBean
    lateinit var plccCardRewardsPublishService: PlccCardRewardsPublishService

    @MockBean
    lateinit var plccCardRewardsService: PlccCardRewardsService

    val now = DateTimeUtil.utcNowLocalDateTime()
    val executionContext = CollectExecutionContext(
        executionRequestId = "UUID",
        organizationId = "card",
        userId = "1",
        startAt = null
    )

    @MockBean
    lateinit var plccCardTransactionService: PlccCardTransactionService

    @MockBean
    lateinit var plccCardTransactionPublishService: PlccCardTransactionPublishService

    @BeforeEach
    fun before() {
        given(uuidService.generateExecutionRequestId()).willReturn("UUID")
        given(organizationService.getOrganizationByObjectId("card")).willReturn(
            CardOrganization()
                .apply {
                    this.organizationId = "card"
                    this.organizationObjectId = "card"
                }
        )
    }

    @Test
    @DisplayName("DI Test")
    fun init() {
        Assertions.assertNotNull(collectcardGrpcService)
    }

    @Test
    @DisplayName("???????????? ?????????")
    fun healthCheckTest() {
        val request = CollectcardProto.HealthCheckRequest.newBuilder().build()
        val responseObserver: StreamRecorder<CollectcardProto.HealthCheckResponse> = StreamRecorder.create()

        collectcardGrpcService.healthCheck(request, responseObserver)
        val results: List<CollectcardProto.HealthCheckResponse> = responseObserver.values
        Assertions.assertEquals(1, results.size)

        val response: CollectcardProto.HealthCheckResponse = results[0]
        Assertions.assertEquals(CollectcardProto.HealthCheckResponse.newBuilder().build(), response)
    }

    @Test
    @DisplayName("???????????? ?????????")
    fun listCardsUnitTest() {
        // given
        val responseObserver: StreamRecorder<CollectcardProto.ListCardsResponse> = StreamRecorder.create()
        val nowLocalDateTime = NowUtcLocalDatetime()
        val request = CollectcardProto
            .ListCardsRequest
            .newBuilder()
            .setCompanyId(StringValue.of("card"))
            .setUserId("1")
            .build()

        val response = ListCardsResponse(
            resultCodes = mutableListOf(ResultCode.OK),
            dataBody = ListCardsResponseDataBody(
                nextKey = "",
                cards = mutableListOf(Card().apply {
                    this.cardCompanyCardId = "9523*********8721"
                    this.cardOwnerName = "?????????"
                    this.cardOwnerType = CardOwnerType.SELF
                    this.cardOwnerTypeOrigin = "1"
                    this.cardName = "Deep Store[??? ?????????]"
                    this.internationalBrandName = "CARD_INTERNATIONAL_BRAND_MASTERCARD"
                    this.cardNumber = "9523-****-*****-8721"
                    this.cardNumberMask = "9523-****-*****-8721"
                    this.cardType = CardType.CREDIT
                    this.cardTypeOrigin = "1"
                    this.isBusinessCard = false
                    this.isTrafficSupported = true
                })
            ),
            dataHeader = ListCardsResponseDataHeader(
                resultCode = ResultCode.OK,
                resultMessage = ""
            )
        )
        given(localDatetimeService.generateNowLocalDatetime()).willReturn(nowLocalDateTime)
        given(cardService.listCards(executionContext, nowLocalDateTime.now)).willReturn(response)

        // when
        collectcardGrpcService.listCards(request, responseObserver)

        // then
        val expect = response.toListCardsResponseProto()
        val results: List<CollectcardProto.ListCardsResponse> = responseObserver.values

        Assertions.assertEquals(1, results.size)
        Assertions.assertEquals(expect, results[0])
    }

    @Test
    @DisplayName("?????? ???????????? ?????? ?????????")
    fun listCardTransactionsUnitTest() {

        // given
        val responseObserver: StreamRecorder<CollectcardProto.ListCardTransactionsResponse> = StreamRecorder.create()
        val nowLocalDateTime = NowUtcLocalDatetime()
        val request = CollectcardProto.ListCardTransactionsRequest
            .newBuilder()
            .setCardId(StringValue.of("card"))
            .setCompanyId(StringValue.of("card"))
            .setUserId("1")
            .build()

        val response = ListTransactionsResponse(
            resultCodes = mutableListOf(),
            dataHeader = ListTransactionsResponseDataHeader(
                successCode = null,
                resultCode = ResultCode.OK,
                resultMessage = ""
            ),
            dataBody = ListTransactionsResponseDataBody(
                nextKey = "",
                transactions = mutableListOf(CardTransaction().apply {
                    this.cardNumber = "687"
                    this.cardNumberMask = "687"
                    this.cardCompanyCardId = ""
                    this.storeName = "?????????(GS)25 ????????????"
                    this.storeNumber = "0086807609"
                    this.cardType = CardType.CREDIT
                    this.cardTransactionType = CardTransactionType.PURCHASE
                    this.cardTransactionTypeOrigin = "1"
                    this.currencyCode = "KRW"
                    this.isInstallmentPayment = true
                    this.installment = 3
                    this.amount = BigDecimal("2000")
                    this.canceledAmount = BigDecimal("0")
                    this.partialCanceledAmount = BigDecimal("0")
                    this.approvalNumber = "26591309"
                    this.approvalDay = "20200709"
                    this.approvalTime = "131320"
                    this.paymentDay = "20200725"
                })
            )
        )
        given(localDatetimeService.generateNowLocalDatetime()).willReturn(nowLocalDateTime)
        given(cardTransactionService.listTransactions(executionContext, nowLocalDateTime.now)).willReturn(response)

        // when
        collectcardGrpcService.listCardTransactions(request, responseObserver)

        // then
        val results = responseObserver.values
        val expect = response.toListCardsTransactionResponseProto()
        Assertions.assertEquals(1, results.size)
        Assertions.assertEquals(expect, results[0])
    }

    @Test
    fun listCardBillsUnitTest() {

        // given
        val responseObserver: StreamRecorder<CollectcardProto.ListCardBillsResponse> = StreamRecorder.create()

        val request = CollectcardProto.ListCardBillsRequest
            .newBuilder()
            .setUserId("1")
            .setCompanyId(StringValue.of("card"))
            .build()

        val response = ListCardBillsResponse(
            resultCodes = mutableListOf(),
            dataHeader = ListCardBillsResponseDataHeader(
                successCode = "OK",
                resultCode = ResultCode.OK,
                resultMessage = ""
            ),
            dataBody = ListCardBillsResponseDataBody(
                nextKey = "",
                cardBills = mutableListOf(CardBill().apply {
                    this.billNumber = "202008140001"
                    this.billType = "0001"
                    this.cardType = BillCardType.DEBIT
                    this.userName = ""
                    this.userGrade = ""
                    this.paymentDay = "20200814"
                    this.billedYearMonth = "202008"
                    this.nextPaymentDay = ""
                    this.paymentBankId = "088"
                    this.paymentAccountNumber = "11031******0"
                })
            )
        )
        val nowLocalDateTime = NowUtcLocalDatetime()
        given(localDatetimeService.generateNowLocalDatetime()).willReturn(nowLocalDateTime)
        given(cardBillService.listUserCardBills(executionContext, nowLocalDateTime.now)).willReturn(response)

        // when
        collectcardGrpcService.listCardBills(request, responseObserver)

        // then
        val results = responseObserver.values
        val expect = response.toListCardBillsResponseProto()

        Assertions.assertEquals(1, results.size)
        Assertions.assertEquals(expect, results[0])
    }

    @Test
    @DisplayName("?????? ??? ?????? ?????????")
    fun listCardLoansUnitTest() {

        // given
        val responseObserver: StreamRecorder<CollectcardProto.ListCardLoansResponse> = StreamRecorder.create()
        val nowLocalDateTime = NowUtcLocalDatetime()
        responseObserver.values

        val request = CollectcardProto.ListCardLoansRequest
            .newBuilder()
            .setCompanyId(StringValue.of("card"))
            .setUserId("1")
            .build()

        val response = ListLoansResponse(
            resultCodes = mutableListOf(),
            dataHeader = ListLoansResponseDataHeader(
                successCode = null,
                resultMessage = "",
                resultCode = ResultCode.OK
            ),
            dataBody = ListLoansResponseDataBody(
                nextKey = "",
                loans = mutableListOf(Loan().apply {
                    this.loanId = "0005"
                    this.loanNumber = "0005"
                    this.loanName = "?????????????????????"
                    this.loanAmount = BigDecimal("3000000")
                    this.remainingAmount = BigDecimal("3000000")
                    this.issuedDay = "20200225"
                    this.expirationDay = "20200728"
                    this.repaymentMethodOrigin = "05" // 290 PR ?????? ???, ????????????
                    this.interestRate = BigDecimal("14.9")
                    this.dataHeader = LoanResponseDataHeader().apply {
                        this.resultCode = ResultCode.OK
                        this.resultMessage = "??????????????? ?????? ???????????????."
                    }
                })
            )
        )
        given(localDatetimeService.generateNowLocalDatetime()).willReturn(nowLocalDateTime)
        given(cardLoanService.listCardLoans(executionContext, nowLocalDateTime.now)).willReturn(response)

        // when
        collectcardGrpcService.listCardLoans(request, responseObserver)

        // given
        val results = responseObserver.values
        val expect = response.toListCardLoansResponseProto()
        Assertions.assertEquals(1, results.size)
        Assertions.assertEquals(expect, results[0])
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????????")
    fun creditLimitUnitTest() {
        //  given
        val responseObserver: StreamRecorder<CollectcardProto.GetCreditLimitResponse> = StreamRecorder.create()
        val nowLocalDateTime = NowUtcLocalDatetime()

        val request = CollectcardProto.GetCreditLimitRequest
            .newBuilder()
            .setCompanyId(StringValue.of("card"))
            .setUserId("1")
            .build()

        val response = CreditLimitResponse(
            dataHeader = CreditLimitResponseDataHeader(
                resultCode = ResultCode.OK,
                resultMessage = ""
            ),
            dataBody = CreditLimitResponseDataBody(
                creditLimitInfo = CreditLimit().apply {
                    this.loanLimit = Limit().apply {
                        this.totalLimitAmount = BigDecimal("10000000")
                        this.remainedAmount = BigDecimal("9966500")
                        this.usedAmount = BigDecimal("33500")
                    }
                    this.onetimePaymentLimit = Limit().apply {
                        this.totalLimitAmount = BigDecimal("10000000")
                        this.remainedAmount = BigDecimal("9966500")
                        this.usedAmount = BigDecimal("33500")
                    }
                    this.cardLoanLimit = Limit().apply {
                        this.totalLimitAmount = BigDecimal("3000000")
                        this.remainedAmount = BigDecimal("3000000")
                        this.usedAmount = BigDecimal("0")
                    }
                    this.creditCardLimit = null
                    this.debitCardLimit = null
                    this.cashServiceLimit = Limit().apply {
                        this.totalLimitAmount = BigDecimal("3000000")
                        this.remainedAmount = BigDecimal("3000000")
                        this.usedAmount = BigDecimal("0")
                    }
                    this.overseaLimit = null
                    this.installmentLimit = Limit().apply {
                        this.totalLimitAmount = BigDecimal("10000000")
                        this.remainedAmount = BigDecimal("9966500")
                        this.usedAmount = BigDecimal("0")
                    }
                }
            )
        )

        given(localDatetimeService.generateNowLocalDatetime()).willReturn(nowLocalDateTime)
        given(cardCreditLimitService.cardCreditLimit(executionContext, nowLocalDateTime.now)).willReturn(response)

        // when
        collectcardGrpcService.getCreditLimit(request, responseObserver)

        val results = responseObserver.values
        val expect = response.toCreditLimitResponseProto()

        Assertions.assertEquals(1, results.size)
        Assertions.assertEquals(expect, results[0])
    }

    @Test
    @DisplayName("?????? ????????? ?????????")
    fun getSyncStatusUnitTest() {

        //  given
        val responseObserver: StreamRecorder<CollectcardProto.GetSyncStatusResponse> = StreamRecorder.create()

        val request = CollectcardProto.GetSyncStatusRequest.newBuilder()
            .setCompanyId(StringValue.of("card"))
            .setUserId("1")
            .build()

        val response = UserSyncStatusResponse().apply {
            this.dataBody = mutableListOf(SyncStatusResponse().apply {
                this.userId = 1
                this.companyId = "card"
                this.companyType = "card"
                this.syncedAt = 1L
            })
        }

        given(userSyncStatusService.getUserSyncStatus(executionContext)).willReturn(response)

        // when
        collectcardGrpcService.getSyncStatus(request, responseObserver)

        // then
        val results: List<CollectcardProto.GetSyncStatusResponse> = responseObserver.values
        val expect = response.toSyncStatusResponseProto()

        Assertions.assertEquals(1, results.size)
        Assertions.assertEquals(expect, results[0])
    }

    @Test
    @DisplayName("?????? ????????? ?????? ?????????")
    fun deleteSyncStatusUnitTest() {

        //  given
        val responseObserver: StreamRecorder<CollectcardProto.DeleteSyncStatusResponse> = StreamRecorder.create()

        val request = CollectcardProto.DeleteSyncStatusRequest.newBuilder()
            .setCompanyId(StringValue.of("card"))
            .setUserId("1")
            .build()

        doNothing().`when`(userSyncStatusService).updateDeleteFlagByUserIdAndCompanyId(executionContext)

        // when
        collectcardGrpcService.deleteSyncStatus(request, responseObserver)

        // then
        val results: List<CollectcardProto.DeleteSyncStatusResponse> = responseObserver.values
        val expect = CollectcardProto.DeleteSyncStatusResponse.newBuilder().build()

        Assertions.assertEquals(1, results.size)
        Assertions.assertEquals(expect, results[0])
    }

    @Test
    @DisplayName("?????? ????????? ?????? ?????? ?????????")
    fun deleteAllSyncStatusUnitTest() {

        //  given
        val responseObserver: StreamRecorder<CollectcardProto.DeleteAllSyncStatusResponse> = StreamRecorder.create()

        val request = CollectcardProto.DeleteAllSyncStatusRequest.newBuilder()
            .setUserId("1")
            .build()

        doNothing().`when`(userSyncStatusService).updateDeleteFlagByUserId(executionContext.userId.toLong())

        // when
        collectcardGrpcService.deleteAllSyncStatus(request, responseObserver)

        // then
        val results: List<CollectcardProto.DeleteAllSyncStatusResponse> = responseObserver.values
        val expect = CollectcardProto.DeleteAllSyncStatusResponse.newBuilder().build()

        Assertions.assertEquals(1, results.size)
        Assertions.assertEquals(expect, results[0])
    }

    @Test
    @DisplayName("PLCC ?????? ?????? ?????? ?????????")
    fun plccTransactionUnitTest() {
    }
}
