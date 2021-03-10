package com.rainist.collectcard.common.collect.api

import com.rainist.collect.common.api.Api
import com.rainist.collect.common.api.Api.request
import com.rainist.collect.common.api.Api.response
import com.rainist.collect.common.api.SignaturePolicy
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class LottecardPlccApis {

    @Value("\${lottecard.plcc.host}")
    lateinit var lottecardHost: String

    @PostConstruct
    fun init() {

        // PLCC 혜택 적용 내역 조회
        card_lottecard_plcc_transactions =
            Api.builder()
                .id("card_lottecard_plcc_transactions")
                .name("03-1 혜택 적용내역 조회 (실시간)")
                .signaturePolicy(signaturePolicyLottecard)
                .endpoint("$lottecardHost/banksalad/service-benefit-info")
                .method(Api.HttpMethod.POST)
                .transform(
                    request(
                        "transform/card/lottecard/plcc/header_req.jslt",
                        "transform/card/lottecard/plcc/transaction_03-1_req.jslt"
                    ),
                    response(
                        "transform/card/lottecard/plcc/header_res.jslt",
                        "transform/card/lottecard/plcc/transaction_03-1_res.jslt"
                    )
                ).build()

        // PLCC 혜택 실적 한도 조회
        card_lottecard_plcc_rewards =
            Api.builder()
                .id("card_lottecard_plcc_rewards")
                .name("02-1 혜택 실적한도 조회 (실시간)")
                .signaturePolicy(signaturePolicyLottecard)
                .endpoint("$lottecardHost/banksalad/service-limit-info")
                .method(Api.HttpMethod.POST)
                .transform(
                    request(
                        "transform/card/lottecard/plcc/header_req.jslt",
                        "transform/card/lottecard/plcc/reward_02-1_req.jslt"
                    ),
                    response(
                        "transform/card/lottecard/plcc/header_res.jslt",
                        "transform/card/lottecard/plcc/reward_02-1_res.jslt"
                    )
                ).build()
    }

    companion object {
        private val signaturePolicyLottecard = SignaturePolicy.builder()
            .algorithm(SignaturePolicy.Algorithm.NONE)
            .build()

        // PLCC 롯데카드 혜택 적용 내역조회
        lateinit var card_lottecard_plcc_transactions: Api

        // PLCC 롯데카드 혜택 실적 한도조회
        lateinit var card_lottecard_plcc_rewards: Api
    }
}
