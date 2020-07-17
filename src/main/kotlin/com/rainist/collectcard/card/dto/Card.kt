package com.rainist.collectcard.card.dto

import java.math.BigDecimal
import java.time.ZonedDateTime

enum class Brand {
    ShinhanCard
}

enum class CardStatus {
    REGISTERED,
    TERMINATED,
    DORMANT,
    SUSPENDED
}

enum class CardOwnerType {
    SELF,
    FAMILY,
    BUSINESS
}

data class Card(
    var cardId: String?,

    var cardCompanyId: String?,

    // TODO : add IDL
    // 카드사 카드 아이디
    var cardCompanyCardId: String?,

    // 카드 소유주 이름 (e.g. 김뱅샐)
    var cardOwnerName: String?,

    // 카드 소유자 구분
    var cardOwnerType: CardOwnerType?,

    // 카드 이름 (e.g. 나라사랑카드)
    var cardName: String?,

    // 카드 브랜드 (e.g. KB, Woori, etc.)
    var cardBrandName: String?,

    // 국제 브랜드 (e.g. VISA, MasterCard, AMEX, etc)
    var internationalBrandName: String?,

    // 카드 번호 마스크 (e.g. 1111-1100-0000-1111, 1:유효숫자 부분, 0:마스크된 부분)
    var cardNumber: String?,

    // 카드 번호 (e.g. 9430-20**-****-2399)
    var cardNumberMask: String?,

    // 카드 타입 (e.g. 신용카드, 체크카드)
    var cardType: String?,

    // 카드 발급일자
    var issuedAt: ZonedDateTime?,

    // 카드 만료일자
    var expiresAt: ZonedDateTime?,

    // 카드 상태 (e.g. 등록, 해지, 휴면, 거래정지)
    var cardStatus: CardStatus?,

    // 마지막 이용일
    var lastUseDate: ZonedDateTime?,

    // 연회비
    var annualFee: BigDecimal?,

    // 결제기관
    var paymentBankId: String?,

    // 결제계좌번호
    var paymentAccountNumber: String?,

    // 법인카드 여부
    var isBusinessCard: Boolean = false
)
