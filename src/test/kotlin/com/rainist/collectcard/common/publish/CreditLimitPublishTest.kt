package com.rainist.collectcard.common.publish

import com.rainist.collectcard.cardcreditlimit.CardCreditLimitService
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimit
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.dto.CollectExecutionContext
import com.rainist.collectcard.common.publish.banksalad.CreditLimitPublishService
import com.rainist.collectcard.common.service.HeaderService
import com.rainist.collectcard.common.util.ExecutionTestUtil
import com.rainist.collectcard.common.util.ReflectionCompareUtil
import com.rainist.common.log.Log
import com.rainist.common.util.DateTimeUtil
import java.util.UUID
import javax.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestTemplate

@ExtendWith(SpringExtension::class)
@SpringBootTest
@DisplayName("CreditLimitPublish 테스트")
class CreditLimitPublishTest {

    companion object : Log

    @Autowired
    lateinit var creditLimitPublishService: CreditLimitPublishService

    @Autowired
    lateinit var commonRestTemplate: RestTemplate

    @Autowired
    lateinit var creditLimitService: CardCreditLimitService

    @MockBean
    lateinit var headerService: HeaderService

    @Test
    @Rollback
    @Transactional
    fun creditLimitShadowingTest() {

        val userId = 12345L
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()
        val now = DateTimeUtil.utcNowLocalDateTime()
        val organizationId = "shinhancard"
        ExecutionTestUtil.serverSetting(
            server,
            ShinhancardApis.card_shinhancard_credit_limit,
            "classpath:mock/shinhancard/creditlimit/card_credit_limit_expected_1.json"
        )

        val executionContext: CollectExecutionContext = CollectExecutionContext(
            executionRequestId = UUID.randomUUID().toString(),
            organizationId = organizationId,
            userId = userId.toString()
        )

        BDDMockito.given(headerService.makeHeader(executionContext.userId, executionContext.organizationId))
            .willReturn(
                mutableMapOf(
                    "contentType" to MediaType.APPLICATION_JSON_VALUE,
                    "authorization" to "Bearer 123",
                    "clientId" to "596d66692c4069c168b57c59"
                )
            )

        val oldResponse = creditLimitService.cardCreditLimit(executionContext, now)

        val shadowingResponse = creditLimitPublishService.shadowing(
            userId,
            organizationId,
            now,
            executionContext.executionRequestId,
            oldResponse
        )

        // DTO에 있지만 엔티티에 없는 field 추가로 isDiff 결과는 false
        val isDiff = shadowingResponse.isDiff
        assertThat(isDiff).isFalse()

        val oldCreditLimit = shadowingResponse.oldResponse as CreditLimit
        val shadowingCreditLimit = shadowingResponse.shadowingResponse as CreditLimit

        if (isDiff) {
            val diffFieldMap =
                ReflectionCompareUtil.reflectionCompareCreditLimit(oldCreditLimit, shadowingCreditLimit)
            logger.info("diffFieldMap = {}", diffFieldMap.toString())
        }

        // loanLimit
        val loanLimit = oldCreditLimit.loanLimit
        val shadowingLoanLimit = shadowingCreditLimit.loanLimit
        val isDiffLoanLimit = listOf(
            loanLimit?.totalLimitAmount == shadowingLoanLimit?.totalLimitAmount,
            loanLimit?.remainedAmount == shadowingLoanLimit?.remainedAmount,
            loanLimit?.usedAmount == shadowingLoanLimit?.usedAmount
        ).all { it }

        // onetimePaymentLimit
        val onetimePaymentLimit = oldCreditLimit.onetimePaymentLimit
        val shadowingOnetimePaymentLimit = shadowingCreditLimit.onetimePaymentLimit
        val isDiffOnetimePaymentLimit = listOf(
            onetimePaymentLimit?.totalLimitAmount == shadowingOnetimePaymentLimit?.totalLimitAmount,
            onetimePaymentLimit?.remainedAmount == shadowingOnetimePaymentLimit?.remainedAmount,
            onetimePaymentLimit?.usedAmount == shadowingOnetimePaymentLimit?.usedAmount
        ).all { it }

        // cardLoanLimit
        val cardLoanLimit = oldCreditLimit.cardLoanLimit
        val shadowingCardLoanLimit = shadowingCreditLimit.cardLoanLimit
        val isDiffCardLoanLimit = listOf(
            cardLoanLimit?.totalLimitAmount == shadowingCardLoanLimit?.totalLimitAmount,
            cardLoanLimit?.remainedAmount == shadowingCardLoanLimit?.remainedAmount,
            cardLoanLimit?.usedAmount == shadowingCardLoanLimit?.usedAmount
        ).all { it }

        // creditCardLimit : 엔티티와 dto 둘 다 null이기 때문에 null인지만 비교
        val creditCardLimit = oldCreditLimit.creditCardLimit
        val shadowingCreditCardLimit = shadowingCreditLimit.creditCardLimit
        val isDiffCreditCardLimit = listOf(
            creditCardLimit == shadowingCreditCardLimit
        ).all { it }

        // debitCardLimit : 엔티티와 dto 둘 다 null이기 때문에 null인지만 비교
        val debitCardLimit = oldCreditLimit.debitCardLimit
        val shadowingDebitCardLimit = shadowingCreditLimit.debitCardLimit
        val isDiffDebitCardLimit = listOf(
            debitCardLimit == shadowingDebitCardLimit
        ).all { it }

        // cashServiceLimit
        val cashServiceLimit = oldCreditLimit.cashServiceLimit
        val shadowingCashServiceLimit = shadowingCreditLimit.cashServiceLimit
        val isDiffCashServiceLimit = listOf(
            cashServiceLimit?.totalLimitAmount == shadowingCashServiceLimit?.totalLimitAmount,
            cashServiceLimit?.remainedAmount == shadowingCashServiceLimit?.remainedAmount,
            cashServiceLimit?.usedAmount == shadowingCashServiceLimit?.usedAmount
        ).all { it }

        // overseaLimit : 엔티티와 dto 둘 다 null이기 때문에 null인지만 비교
        val overseaLimit = oldCreditLimit.overseaLimit
        val shadowingOverseaLimit = shadowingCreditLimit.overseaLimit
        val isDiffOverseaLimit = listOf(
            overseaLimit == shadowingOverseaLimit
        ).all { it }

        val installmentLimit = oldCreditLimit.installmentLimit
        val shadowingInstallmentLimit = shadowingCreditLimit.installmentLimit
        val isDiffInstallmentLimit = listOf(
            installmentLimit?.totalLimitAmount == shadowingInstallmentLimit?.totalLimitAmount,
            installmentLimit?.remainedAmount == shadowingInstallmentLimit?.remainedAmount,
            installmentLimit?.usedAmount == shadowingInstallmentLimit?.usedAmount
        ).all { it }

        assertAll(
            "CreditLimit Shadowing",
            { assertThat(isDiffLoanLimit).isTrue() },
            { assertThat(isDiffOnetimePaymentLimit).isTrue() },
            { assertThat(isDiffCardLoanLimit).isTrue() },
            { assertThat(isDiffCreditCardLimit).isTrue() },
            { assertThat(isDiffDebitCardLimit).isTrue() },
            { assertThat(isDiffCashServiceLimit).isTrue() },
            { assertThat(isDiffOverseaLimit).isTrue() },
            { assertThat(isDiffInstallmentLimit).isTrue() }
        )
    }
}
