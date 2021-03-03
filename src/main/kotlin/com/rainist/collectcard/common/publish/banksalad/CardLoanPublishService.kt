package com.rainist.collectcard.common.publish.banksalad

import com.rainist.collectcard.cardloans.dto.ListLoansResponse
import com.rainist.collectcard.cardloans.dto.Loan
import com.rainist.collectcard.cardloans.mapper.CardLoanMapper
import com.rainist.collectcard.common.db.repository.CardLoanRepository
import com.rainist.collectcard.common.dto.CollectShadowingResponse
import com.rainist.collectcard.common.util.ReflectionCompareUtil
import com.rainist.common.log.Log
import java.time.LocalDateTime
import org.apache.commons.lang3.builder.EqualsBuilder
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service

@Service
class CardLoanPublishService(
    val cardLoanRepository: CardLoanRepository
) {

    companion object : Log

    val cardLoanMapper = Mappers.getMapper(CardLoanMapper::class.java)

    fun sync(banksaladUserId: Long, organizationId: String, lastCheckAt: LocalDateTime) {
        TODO()
    }

    fun shadowing(
        banksaladUserId: Long,
        organizationId: String,
        lastCheckAt: LocalDateTime?,
        executionRequestId: String,
        oldResponse: ListLoansResponse
    ): CollectShadowingResponse {
        val loans = cardLoanRepository.findAllByBanksaladUserIdAndCardCompanyIdAndLastCheckAt(
            banksaladUserId, organizationId, lastCheckAt
        ).map {
            cardLoanMapper.toLoanDto(it)
        }.sortedWith(compareBy({ it.loanNumber }, { it.expirationDay }))

        val oldLoans = oldResponse.dataBody?.loans?.sortedWith(compareBy({ it.loanNumber }, { it.expirationDay }))
            ?: mutableListOf()
        val isShadowingDiff = unequals(loans, oldLoans)

        if (isShadowingDiff && (oldLoans.size == loans.size)) {
            val diffFieldMap = ReflectionCompareUtil.reflectionCompareCardLoans(oldLoans, loans)
            logger.With("diff_field_map", diffFieldMap.toString())
        }

        return CollectShadowingResponse(
            banksaladUserId = banksaladUserId,
            organizationId = organizationId,
            lastCheckAt = lastCheckAt.toString(),
            executionRequestId = executionRequestId,
            isDiff = isShadowingDiff,
            executionName = "loans",
            oldList = oldLoans,
            dbList = loans
        )
    }

    private fun unequals(lhs: List<Loan>, rhs: List<Loan>): Boolean {
        if (lhs.size != rhs.size)
            return true

        for (i in lhs.indices) {
            if (!EqualsBuilder.reflectionEquals(lhs[i], rhs[i])) {
                return true
            }
        }
        return false
    }
}
