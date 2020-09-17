
package com.rainist.collectcard.cardtransaction

import com.rainist.collectcard.cardtransactions.CardTransactionServiceImpl
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsRequest
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsRequestDataBody
import com.rainist.collectcard.common.collect.api.Transaction
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.service.UserSyncStatusService
import com.rainist.common.exception.ValidationException
import com.rainist.common.util.DateTimeUtil
import java.util.UUID
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@DisplayName("카드이용내역 상속 서비스 테스트")
class CardTransactionServiceImplTest {

    @Autowired
    lateinit var cardTransactionServiceImpl: CardTransactionServiceImpl

    @Autowired
    lateinit var userSyncStatusService: UserSyncStatusService

    @MockBean
    lateinit var headerService: HeaderService

    @Test
    @DisplayName("거래 내역 조회 시작일 기본 생성 테스트")
    fun startAtTest() {
        val researchInterval = 3L
        val userId = DateTimeUtil.kstLocalDateTimeToEpochMilliSecond().toString()
        val lastLocalDate = DateTimeUtil.stringToLocalDate("20200915", "yyyyMMdd")
        val lastCheckAt = DateTimeUtil.utcLocalDateToEpochMilliSecond(lastLocalDate)

        // given
        val executionContext = requestSetting(userId).apply {
            this.startAt = null
        }

        userSyncStatusService.upsertUserSyncStatus(
            executionContext.userId.toLong(),
            executionContext.organizationId,
            Transaction.cardTransaction.name,
            lastCheckAt,
            true
        )

        // when
        val result = cardTransactionServiceImpl.getStartAt(executionContext)

        // then
        val expect = DateTimeUtil.localDateToString(lastLocalDate.minusDays(researchInterval), "yyyyMMdd")

        Assertions.assertEquals(expect, result)
    }

    @Test
    @DisplayName("거래 내역 조회 시작일자 6개월 생성 테스트")
    fun startAtDefaultMaxTest() {
        val MAX = 6

        // given
        val executionContext = requestSetting(DateTimeUtil.kstLocalDateTimeToEpochMilliSecond().toString()).apply {
            this.startAt = null
        }

        // when
        val result = cardTransactionServiceImpl.getStartAt(executionContext)

        // then
        val expect = DateTimeUtil.localDateToString(DateTimeUtil.kstNowLocalDate().minusMonths(MAX.toLong()), "yyyyMMdd")

        Assertions.assertEquals(expect, result)
    }

    @Test
    @DisplayName("Async 거래내역 일자 분할 생성 테스트")
    fun getSearchDateListTest() {

        // given
        val userId = DateTimeUtil.kstLocalDateTimeToEpochMilliSecond().toString()
        val executionContext = requestSetting(userId).apply {
            this.startAt = null
        }
        val request = ListTransactionsRequest().apply {
            this.dataBody = ListTransactionsRequestDataBody().apply {
                this.startAt = "20200101"
                this.endAt = "20201231"
            }
        }

        // when
        val result = cardTransactionServiceImpl.getSearchDateList(executionContext, request)

        Assertions.assertEquals(result.size, 6)
    }

    @Test
    @DisplayName("Async 거래내역 일자 분할 실패 테스트")
    fun getSearchDateListFailTest() {

        // given
        val userId = DateTimeUtil.kstLocalDateTimeToEpochMilliSecond().toString()
        val executionContext = requestSetting(userId).apply {
            this.startAt = null
        }

        val request = ListTransactionsRequest().apply {
            this.dataBody = ListTransactionsRequestDataBody().apply {
            }
        }

        // when
        val exception = Assertions.assertThrows(ValidationException::class.java) {
            cardTransactionServiceImpl.getSearchDateList(executionContext, request)
        }

        Assertions.assertNotNull(exception)
        Assertions.assertEquals(exception.javaClass, ValidationException::class.java)
    }

    private fun requestSetting(banksaladUserId: String?): CollectExecutionContext {

        val executionContext = CollectExecutionContext(
            executionRequestId = UUID.randomUUID().toString(),
            organizationId = "shinhancard",
            userId = banksaladUserId ?: "1",
            startAt = DateTimeUtil.utcNowLocalDateTime()
        )

        BDDMockito.given(headerService.makeHeader(executionContext.userId, executionContext.organizationId))
            .willReturn(
                mutableMapOf(
                    "contentType" to MediaType.APPLICATION_JSON_VALUE,
                    "authorization" to "Bearer 123",
                    "clientId" to "596d66692c4069c168b57c59"
                )
            )
        return executionContext
    }
}
