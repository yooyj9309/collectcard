package com.rainist.collectcard.cardloans.dto

import com.rainist.collectcard.common.enums.CardLoanRepaymentMethod
import com.rainist.collectcard.common.enums.CardLoanStatus
import com.rainist.collectcard.common.enums.ResultCode
import com.rainist.common.annotation.validation.StringDateFormat
import java.math.BigDecimal
import org.springframework.format.annotation.NumberFormat

data class Loan(

    var loanId: String? = null, // 대출 id

    var loanNumber: String? = null, // 대출번호

    var loanName: String? = null, // 상품명

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    var loanAmount: BigDecimal? = null, // 대출금액

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    var remainingAmount: BigDecimal? = null, // 대출잔액

    var paymentBankId: String? = null, // 결제기관

    var paymentAccountNumber: String? = null, // 결제계좌번호

    @field:StringDateFormat("yyyyMMdd")
    var issuedDay: String? = null, // 신규일 (개설일)

    @field:StringDateFormat("yyyyMMdd")
    var expirationDay: String? = null, // 만기일

    var loanStatus: CardLoanStatus = CardLoanStatus.CardLOAN_STATUS_UNKNOWN, // 상태 (미납, 완납)

    var loanStatusOrigin: String? = null,

    var repaymentMethod: CardLoanRepaymentMethod = CardLoanRepaymentMethod.UNKNOWN, // 상환방식

    var repaymentMethodOrigin: String? = null,

    var withdrawalDay: String? = null, // 출금일자

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    var interestRate: BigDecimal? = null, // 이자율

    var loanCategory: String? = null, // 대출종류

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    var additionalLoanAmount: BigDecimal? = null, // 추가대출 가능액

    @field:StringDateFormat("yyyyMMdd")
    var fullyPaidDay: String? = null, // 대출 완납일

    var cardNumber: String? = null, // 카드번호

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    var principalAmount: BigDecimal? = null, // 상환액중 원금

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    var interestAmount: BigDecimal? = null, // 상환액중 이자

    var dataHeader: LoanResponseDataHeader? = null
)

data class LoanResponseDataHeader(
    var successCode: String? = null,
    var resultCode: ResultCode? = null,
    var resultMessage: String? = null
)
