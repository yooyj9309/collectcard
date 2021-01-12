package com.rainist.collectcard.cardcreditlimit.mapper

import com.rainist.collectcard.cardcreditlimit.dto.CreditLimit
import com.rainist.collectcard.cardcreditlimit.dto.Limit
import com.rainist.collectcard.config.MapStructConfig
import java.math.RoundingMode
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings

@Mapper(uses = [MapStructConfig.BigDecimalConverter::class])
abstract class CreditLimitMapper {

    @Mappings(
        value = [
            Mapping(source = "loanLimit", target = "loanLimit"),
            Mapping(source = "onetimePaymentLimit", target = "onetimePaymentLimit"),
            Mapping(source = "cardLoanLimit", target = "cardLoanLimit"),
            Mapping(source = "creditCardLimit", target = "creditCardLimit"),
            Mapping(source = "debitCardLimit", target = "debitCardLimit"),
            Mapping(source = "cashServiceLimit", target = "cashServiceLimit"),
            Mapping(source = "overseaLimit", target = "overseaLimit"),
            Mapping(source = "installmentLimit", target = "installmentLimit")
        ]
    )
    abstract fun toCreditLimitDto(
        loanLimit: Limit,
        onetimePaymentLimit: Limit,
        cardLoanLimit: Limit,
        creditCardLimit: Limit,
        debitCardLimit: Limit,
        cashServiceLimit: Limit,
        overseaLimit: Limit,
        installmentLimit: Limit
    ): CreditLimit

    /**
     * 엔티티의 소수점을 제거, 신한카드에서 넘어오는 DTO에 없는 필드는 null처리
     */
    @AfterMapping
    fun convertCreditLimitDefaultValue(@MappingTarget creditLimit: CreditLimit) {
        loanLimitRoundingDown(creditLimit)
        cardLoanLimitRoundingDown(creditLimit)
        onetimePaymentLimitRoundingDown(creditLimit)
        creditLimit.creditCardLimit = null
        creditLimit.debitCardLimit = null
        cashServiceRoundingDown(creditLimit)
        creditLimit.overseaLimit = null
        installmentLimitRoundingDown(creditLimit)
    }

    private fun installmentLimitRoundingDown(creditLimit: CreditLimit) {
        val installmentLimit = creditLimit.installmentLimit
        installmentLimit?.totalLimitAmount = installmentLimit?.totalLimitAmount?.setScale(0, RoundingMode.DOWN)
        installmentLimit?.remainedAmount = installmentLimit?.remainedAmount?.setScale(0, RoundingMode.DOWN)
        installmentLimit?.usedAmount = installmentLimit?.usedAmount?.setScale(0, RoundingMode.DOWN)
    }

    private fun onetimePaymentLimitRoundingDown(creditLimit: CreditLimit) {
        val onetimePaymentLimit = creditLimit.onetimePaymentLimit
        onetimePaymentLimit?.totalLimitAmount = onetimePaymentLimit?.totalLimitAmount?.setScale(0, RoundingMode.DOWN)
        onetimePaymentLimit?.remainedAmount = onetimePaymentLimit?.remainedAmount?.setScale(0, RoundingMode.DOWN)
        onetimePaymentLimit?.usedAmount = onetimePaymentLimit?.usedAmount?.setScale(0, RoundingMode.DOWN)
    }

    private fun cashServiceRoundingDown(creditLimit: CreditLimit) {
        val cashServiceLimit = creditLimit.cashServiceLimit
        cashServiceLimit?.totalLimitAmount = cashServiceLimit?.totalLimitAmount?.setScale(0, RoundingMode.DOWN)
        cashServiceLimit?.remainedAmount = cashServiceLimit?.remainedAmount?.setScale(0, RoundingMode.DOWN)
        cashServiceLimit?.usedAmount = cashServiceLimit?.usedAmount?.setScale(0, RoundingMode.DOWN)
    }

    private fun cardLoanLimitRoundingDown(creditLimit: CreditLimit) {
        val cardLoanLimit = creditLimit.cardLoanLimit
        cardLoanLimit?.totalLimitAmount = cardLoanLimit?.totalLimitAmount?.setScale(0, RoundingMode.DOWN)
        cardLoanLimit?.remainedAmount = cardLoanLimit?.remainedAmount?.setScale(0, RoundingMode.DOWN)
        cardLoanLimit?.usedAmount = cardLoanLimit?.usedAmount?.setScale(0, RoundingMode.DOWN)
    }

    private fun loanLimitRoundingDown(creditLimit: CreditLimit) {
        val loanLimit = creditLimit.loanLimit
        loanLimit?.totalLimitAmount = loanLimit?.totalLimitAmount?.setScale(0, RoundingMode.DOWN)
        loanLimit?.remainedAmount = loanLimit?.remainedAmount?.setScale(0, RoundingMode.DOWN)
        loanLimit?.usedAmount = loanLimit?.usedAmount?.setScale(0, RoundingMode.DOWN)
    }
}
