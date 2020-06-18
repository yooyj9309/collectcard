package com.rainist.collectcard.common.collect.api

import com.rainist.collect.common.dto.Api
import com.rainist.collect.common.dto.SignaturePolicy
import com.rainist.collectcard.common.collect.api.Apis.Companion.readText
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value

class ShinhancardApis {
    @Value("\${host.card.shinhancard}")
    lateinit var mHostCardShinhancard: String

    @PostConstruct
    fun init() {
        ShinhancardApis.hostCardShinhancard = mHostCardShinhancard
    }

    companion object {
        var hostCardShinhancard: String? = ""

        private val signaturePolicyShinhancard = SignaturePolicy.builder()
            .algorithm(SignaturePolicy.Algorithm.NONE)
            .build()

        // 유효카드 정보조회 SHC_HPG00548
        val card_shinhancard_cards: Api =
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.cards.name)
                .signaturePolicy(signaturePolicyShinhancard)
                .endpoint("$hostCardShinhancard/v1.0/EXT/mycard/searchavailablecard")
                .method(Api.HttpMethod.POST)
                .transformRequestHeader(readText("classpath:transform/card/shinhancard/header_req.jslt"))
                .transformRequestBody(readText("classpath:transform/card/shinhancard/cards_req.jslt"))
                .transformResponseHeader(readText("classpath:transform/card/shinhancard/header_res.jslt"))
                .transformResponseBody(readText("classpath:transform/card/shinhancard/cards_res.jslt"))
                .name("보유카드조회")
                .build()

        // 신용 국내사용내역조회-일시불/할부 SHC_HPG00428
        val card_shinhancard_credit_domestic_transactions: Api =
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.cardTransaction.name)
                .signaturePolicy(signaturePolicyShinhancard)
                .endpoint("$hostCardShinhancard/v1.0/EXT/usecreditcard/searchusefordomestic")
                .method(Api.HttpMethod.POST)
                .transformRequestHeader(readText("classpath:transform/card/shinhancard/header_req.jslt"))
                .transformRequestBody(readText("classpath:transform/card/shinhancard/transaction_SHC_HPG00428_req.jslt"))
                .transformResponseHeader(readText("classpath:transform/card/shinhancard/header_res.jslt"))
                .transformResponseBody(readText("classpath:transform/card/shinhancard/transaction_SHC_HPG00428_res.jslt"))
                .name("신용 국내사용내역 조회-일시불/할부(SHC_HPG00428)")
                .build()
    }
}
