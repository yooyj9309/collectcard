package com.rainist.collectcard.card.dto

import com.rainist.collectcard.common.db.entity.CardEntity
import com.rainist.collectcard.common.enums.CardOwnerType
import com.rainist.collectcard.common.enums.CardStatus
import com.rainist.collectcard.common.enums.CardType
import com.rainist.common.log.Log
import java.math.BigDecimal
import org.apache.commons.lang3.builder.EqualsBuilder

enum class Brand {
    ShinhanCard
}

data class Card(
    var cardId: String? = null,

    var cardCompanyId: String? = null,
    // TODO : add IDL
    // 카드사 카드 아이디
    var cardCompanyCardId: String? = null,
    // 카드 소유주 이름 (e.g. 김뱅샐)
    var cardOwnerName: String? = null,
    // 카드 소유자 구분
    var cardOwnerType: CardOwnerType? = null,
    // 카드 소유자 구분 원본
    var cardOwnerTypeOrigin: String? = null,
    // 카드 이름 (e.g. 나라사랑카드)
    var cardName: String? = null,
    // 카드 브랜드 (e.g. KB, Woori, etc.)
    var cardBrandName: String? = null,
    // 국제 브랜드 (e.g. VISA, MasterCard, AMEX, etc)
    var internationalBrandName: String? = null,
    // 카드 번호 마스크 (e.g. 1111-1100-0000-1111, 1:유효숫자 부분, 0:마스크된 부분)
    var cardNumber: String? = null,
    // 카드 번호 (e.g. 9430-20**-****-2399)
    var cardNumberMask: String? = null,
    // 카드 타입 (e.g. 신용카드, 체크카드)
    var cardType: CardType = CardType.UNKNOWN,
    // 카드 타입 원본 (e.g. 신용카드, 체크카드)
    var cardTypeOrigin: String? = null,
    // 카드 발급일자
    var issuedDay: String? = null,
    // 카드 만료일자
    var expiresDay: String? = null,
    // 카드 상태 (e.g. 등록, 해지, 휴면, 거래정지)
    var cardStatus: CardStatus? = null,
    // 카드 상태 원본 (e.g. 등록, 해지, 휴면, 거래정지)
    var cardStatusOrigin: String? = null,
    // 마지막 이용일자
    var lastUseDay: String? = null,
    // 마지막 이용일시
    var lastUseTime: String? = null,
    // 연회비
    var annualFee: BigDecimal? = null,
    // 결제기관
    var paymentBankId: String? = null,
    // 결제계좌번호
    var paymentAccountNumber: String? = null,
    // 법인카드 여부
    var isBusinessCard: Boolean = false,

    // 교통카드 지원여부 ( db에는 없으나, 현재 신한카드 응답값으로 내리는중)
    var trafficSupported: Boolean = false
) {

    companion object : Log {
        val EXCLUDE_EQUALS_FIELD = mutableListOf(
            CardEntity::cardId.name
        )
    }

    fun unequals(other: Any?): Boolean {

        val unequals = !EqualsBuilder.reflectionEquals(this, other, EXCLUDE_EQUALS_FIELD)

        if (true == unequals) {
            logger.error("[Card Unequals] this-{} : other-{}", this, other)
        }

        return unequals
    }
}
