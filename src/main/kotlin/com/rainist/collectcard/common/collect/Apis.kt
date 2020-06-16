package com.rainist.collectcard.common.collect

import com.rainist.collect.common.dto.Api
import com.rainist.collect.common.dto.SignaturePolicy
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.util.ResourceUtils

enum class BusinessType {
    card
}

enum class Organization {
    shinhancard,
}

enum class Transaction {
    cards, cardbills
}

class Apis() {
    @Value("\${host.card.shinhancard}")
    lateinit var mHostCardShinhancard: String

    @PostConstruct
    fun init() {
        Apis.hostCardShinhancard = mHostCardShinhancard
    }

    companion object {
        var hostCardShinhancard: String = ""

        private val signaturePolicyShinhancard = SignaturePolicy.builder()
            .algorithm(SignaturePolicy.Algorithm.NONE)
            .build()

        val card_shinhancard_cards: Api =
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.cards.name)
                .signaturePolicy(signaturePolicyShinhancard)
                .endpoint(hostCardShinhancard + "/v1.0/EXT/mycard/searchavailablecard")
                .method(Api.HttpMethod.POST)
                .transformRequestHeader(readText("classpath:transform/card/shinhancard/card_shinhancard_header_req.jslt"))
                .transformRequestBody(readText("classpath:transform/card/shinhancard/card_shinhancard_cards_req.jslt"))
                .transformResponseHeader(readText("classpath:transform/card/shinhancard/card_shinhancard_header_res.jslt"))
                .transformResponseBody(readText("classpath:transform/card/shinhancard/card_shinhancard_cards_res.jslt"))
                .name("보유카드조회")
                .build()

        private fun readText(fileInClassPath: String): String {
            return ResourceUtils.getFile(fileInClassPath).readText(Charsets.UTF_8)
        }
    }
}
