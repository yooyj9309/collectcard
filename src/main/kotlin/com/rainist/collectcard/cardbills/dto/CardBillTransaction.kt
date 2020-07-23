package com.rainist.collectcard.cardbills.dto

import com.rainist.collectcard.common.enums.CardTransactionType
import com.rainist.common.annotation.validation.StringDateFormat
import com.rainist.common.annotation.validation.StringTimeFormat
import java.math.BigDecimal
import javax.validation.constraints.NotEmpty
import org.springframework.format.annotation.NumberFormat

data class CardBillTransaction(

    var cardTransactionId: String? = null, //  1. 카드 트렌젝션 Id

    var cardName: String? = null, //  2. 카드이름

    @field:NotEmpty
    var cardNumber: String? = null, //  3. 카드번호

    var businessLicenseNumber: String? = null, //  4. 사업자 번호

    @field:NotEmpty
    var storeName: String? = null, //  5. 가맹점이름

    var storeNumber: String? = null, //  6. 가맹점 번호

    var cardType: String? = null, //  7. 카드타입 (신용카드, 체크카드)

    var cardTransactionType: CardTransactionType? = null, //  8. 내역타입 ( 승인, 전체 취소, 부분취소, 거절 )월

    var currencyCode: String? = null, //  9. 통화코드

    var isInstallmentPayment: Boolean? = null, //  10. 할부여부

    var installment: Int? = null, //  11 할부개월수

    var installmentRound: Int? = null, //  12. 할부회차

    var netSalesAmount: BigDecimal? = null, //  13. 순매출액

    var serviceChargeAmount: BigDecimal? = null, //  14. 봉사료

    var tax: BigDecimal? = null, //  15. 부가세

    var payedPoints: BigDecimal? = null, //  16. 포인트결제금액

    var isPointPay: Boolean? = null, //  17. 포인트결제여부

    var discountAmount: BigDecimal? = null, //  18. 할인금액

    // TODO 예상국 숫자체크로 변경
    @field:NumberFormat(style = NumberFormat.Style.CURRENCY)
    var amount: BigDecimal? = null, //  19. 매출액

    var canceledAmount: BigDecimal? = null, //  20. 취소금액

    @field:NotEmpty
    var approvalNumber: String? = null, //  21. 승인번호

    @field:StringDateFormat(pattern = "yyyyMMdd")
    var approvalDay: String? = null, //  22. 승인일자

    @field:StringTimeFormat(pattern = "HHmmss")
    var approvalTime: String? = null, //  23. 승인시간

    var pointsToEarn: Float? = null, //  24. 적립예정포인트

    var isOverseaUse: Boolean? = null, //  25. 해외사용여부

    var paymentDay: String? = null, //  26. 결제예정일

    var storeCategory: String? = null, // 27. 업종타입

    var transactionCountry: String? = null, // 28. 사용국가

    // TODO : IDL 표준 등록되지 않은 단어임, 등록 후 수정 필요
    var billingRound: Int? = null, //  28. 청구회차
    var paidAmount: BigDecimal? = null, //  29. 입금완납금액
    var thisMonthBilledAmount: BigDecimal? = null, //  30. 당월청구금액
    var thisMonthBilledFee: BigDecimal? = null, //  31. 당월청구금액수수료
    var remainingAmount: BigDecimal? = null, //  32. 청구후잔여금액
    var isPaidFull: Boolean? = null, //  33. 완납여부
    var cashback: BigDecimal? = null, //  34. 캐시백금액
    var pointsRate: Float? = null //  34. 적립예정포인트율
)
