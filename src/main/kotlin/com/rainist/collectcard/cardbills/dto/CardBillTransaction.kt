package com.rainist.collectcard.cardbills.dto

import com.rainist.collectcard.common.enums.CardTransactionType
import com.rainist.collectcard.common.enums.CardType
import com.rainist.common.annotation.validation.StringDateFormat
import com.rainist.common.annotation.validation.StringTimeFormat
import java.math.BigDecimal
import javax.validation.constraints.NotEmpty
import org.springframework.format.annotation.NumberFormat

data class CardBillTransaction(

    var cardTransactionId: String? = null, // 카드 트렌젝션 Id

    var cardCompanyCardId: String? = null, // 카드 id

    var cardName: String? = null, // 카드이름

    @field:NotEmpty
    var cardNumber: String? = null, // 카드번호

    @field:NotEmpty
    var cardNumberMasked: String? = null, // 마스크된 카드번호

    var businessLicenseNumber: String? = null, // 사업자 번호

    @field:NotEmpty
    var storeName: String? = null, // 가맹점이름

    var storeNumber: String? = null, // 가맹점 번호

    var cardType: CardType? = null, // 카드타입 (신용카드, 체크카드)

    var cardTypeOrigin: String? = null, // 카드타입 (신용카드, 체크카드) 원본값

    var cardTransactionType: CardTransactionType? = null, // 내역타입 ( 승인, 전체 취소, 부분취소, 거절 )

    var cardTransactionTypeOrigin: String? = null, // 내역타입 ( 승인, 전체 취소, 부분취소, 거절 ) 원본 값

    var currencyCode: String? = null, // 통화코드

    var isInstallmentPayment: Boolean? = null, // 할부여부

    var installment: Int? = null, //  11 할부개월수

    var installmentRound: Int? = null, // 할부회차

    var netSalesAmount: BigDecimal? = null, // 순매출액

    var serviceChargeAmount: BigDecimal? = null, // 봉사료

    var tax: BigDecimal? = null, // 부가세

    var paidPoints: BigDecimal? = null, // 포인트결제금액

    var isPointPay: Boolean? = null, // 포인트결제여부

    var discountAmount: BigDecimal? = null, // 할인금액

    // TODO 예상국 숫자체크로 변경
    @field:NumberFormat(style = NumberFormat.Style.CURRENCY)
    var amount: BigDecimal? = null, // 매출액

    var canceledAmount: BigDecimal? = null, // 취소금액

    @field:NotEmpty
    var approvalNumber: String? = null, // 승인번호

    @field:StringDateFormat(pattern = "yyyyMMdd")
    var approvalDay: String? = null, // 승인일자

    @field:StringTimeFormat(pattern = "HHmmss")
    var approvalTime: String? = null, // 승인시간

    var pointsToEarn: BigDecimal? = null, // 적립예정포인트

    var isOverseaUse: Boolean? = null, // 해외사용여부

    var paymentDay: String? = null, // 결제예정일

    var storeCategory: String? = null, // 업종타입

    var storeCategoryOrigin: String? = null, // 업종타입 원본값

    var transactionCountry: String? = null, // 사용국가

    var billingRound: Int? = null, // 청구회차

    var paidAmount: BigDecimal? = null, // 입금완납금액

    var billedAmount: BigDecimal? = null, // 당월청구금액

    var billedFee: BigDecimal? = null, // 당월청구금액수수료

    var remainingAmount: BigDecimal? = null, // 청구후잔여금액

    var isPaidFull: Boolean? = null, // 완납여부

    var cashback: BigDecimal? = null, // 캐시백금액

    var pointsRate: BigDecimal? = null, // 적립예정포인트율

    var billNumber: String? = null // 청구서 번호
)
