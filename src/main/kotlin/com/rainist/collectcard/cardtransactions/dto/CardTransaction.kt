package com.rainist.collectcard.cardtransactions.dto

import java.math.BigDecimal

data class CardTransaction(

    var cardTransactionId: String? = null, //  1. 카드 트렌젝션 Id

    var cardName: String? = null, //  2. 카드이름

    var cardNumber: String? = null, //  3. 카드번호

    var businessNumber: String? = null, //  4. 사업자 번호

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

    var amount: BigDecimal? = null, //  19. 매출액

    var canceledAmount: BigDecimal? = null, //  20. 취소금액

    var approvalNumber: Int? = null, //  21. 승인번호

    var approvalDay: String? = null, //  22. 승인일자

    var approvalTime: String? = null, //  23. 승인시간

    var pointsToEarn: Float? = null, //  24. 적립예정포인트

    var isOverseaUse: Boolean? = null, //  25. 해외사용여부

    var paymentDay: String? = null, //  26. 결제예정일

    var storeCategory: String? = null, // 27. 업종타입

    var transactionCountry: String? = null // 28. 사용국가
)
