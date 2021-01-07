package com.rainist.collectcard.cardloans.mapper

import com.rainist.collectcard.cardloans.dto.Loan
import com.rainist.collectcard.common.db.entity.CardLoanEntity
import com.rainist.collectcard.config.MapStructConfig
import java.math.BigDecimal
import java.math.RoundingMode
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings

@Mapper(uses = [MapStructConfig.BigDecimalConverter::class])
abstract class CardLoanMapper {

    @Mappings(
        value = [
            Mapping(source = "cardCompanyLoanId", target = "loanId"),
            Mapping(source = "loanRemainingAmount", target = "remainingAmount")
        ]
    )
    abstract fun toLoanDto(cardLoanEntity: CardLoanEntity): Loan

    @AfterMapping
    fun convertLoanDefaultValue(@MappingTarget loan: Loan) {
        loan.loanAmount = loan.loanAmount?.setScale(0, RoundingMode.DOWN)
        loan.remainingAmount = loan.remainingAmount?.setScale(0, RoundingMode.DOWN)
        loan.interestRate = loan.interestRate?.setScale(1, RoundingMode.DOWN)

        if (loan.loanCategory.isNullOrEmpty()) {
            loan.loanCategory = null
        }
        if (loan.additionalLoanAmount == BigDecimal("0.0000")) {
            loan.additionalLoanAmount = null
        }
        if (loan.principalAmount == BigDecimal("0.0000")) {
            loan.principalAmount = null
        }
        if (loan.interestAmount == BigDecimal("0.0000")) {
            loan.interestAmount = null
        }
    }
}
