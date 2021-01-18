package com.rainist.collectcard.common.publish.banksalad

import com.rainist.collectcard.cardcreditlimit.dto.CreditLimit
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitResponse
import com.rainist.collectcard.cardcreditlimit.dto.Limit
import com.rainist.collectcard.cardcreditlimit.mapper.CreditLimitMapper
import com.rainist.collectcard.common.db.entity.CreditLimitEntity
import com.rainist.collectcard.common.db.repository.CreditLimitRepository
import com.rainist.collectcard.common.dto.SingleCollectShadowingResponse
import com.rainist.common.log.Log
import io.micrometer.core.instrument.MeterRegistry
import java.time.LocalDateTime
import org.apache.commons.lang3.builder.EqualsBuilder
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service

@Service
class CreditLimitPublishService(
    val creditLimitRepository: CreditLimitRepository,
    val meterRegistry: MeterRegistry
) {

    private companion object : Log

    val creditLimitMapper = Mappers.getMapper(CreditLimitMapper::class.java)

    fun sync(banksaladUserId: Long, organizationId: String, lastCheckAt: LocalDateTime) {
        TODO()
    }

    fun shadowing(
        banksaladUserId: Long,
        organizationId: String,
        lastCheckAt: LocalDateTime?,
        executionRequestId: String,
        oldResponse: CreditLimitResponse
    ): SingleCollectShadowingResponse {

        // lastCheckAt 기준으로 조회된 CreditLimitEntity
        val creditLimitEntity = creditLimitRepository.findByBanksaladUserIdAndCardCompanyIdAndLastCheckAt(
            banksaladUserId,
            organizationId,
            lastCheckAt
        ) ?: CreditLimitEntity()

        val creditLimit = creditLimitMapper.toCreditLimitDto(
            Limit.toLoanLimit(creditLimitEntity),
            Limit.toOnetimePaymentLimit(creditLimitEntity),
            Limit.toCardLoanLimit(creditLimitEntity),
            Limit.toCreditCardLimit(creditLimitEntity),
            Limit.toDebitCardLimit(creditLimitEntity),
            Limit.toCashServiceLimit(creditLimitEntity),
            Limit.toOverseaLimit(creditLimitEntity),
            Limit.toInstallmentLimit(creditLimitEntity)
        )

        val oldCreditLimit = oldResponse.dataBody?.creditLimitInfo ?: CreditLimit()
        val isShadowingDiff = !EqualsBuilder.reflectionEquals(creditLimit, oldCreditLimit)

        return SingleCollectShadowingResponse(
            banksaladUserId = banksaladUserId,
            organizationId = organizationId,
            lastCheckAt = lastCheckAt.toString(),
            executionRequestId = executionRequestId,
            isDiff = isShadowingDiff,
            executionName = "creditLimit",
            oldResponse = oldCreditLimit,
            shadowingResponse = creditLimit
        )
    }
}
