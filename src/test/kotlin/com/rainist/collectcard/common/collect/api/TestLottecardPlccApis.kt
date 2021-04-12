package com.rainist.collectcard.common.collect.api

import com.rainist.collect.common.api.Api
import com.rainist.collect.common.api.SignaturePolicy
import org.springframework.stereotype.Component

@Component
class TestLottecardPlccApis {

    companion object {
        fun init(mockServerPort: String) {
            card_lottecard_plcc_rewards =
                Api.builder()
                    .id("card_lottecard_plcc_rewards")
                    .name("02-1 혜택 실적한도 조회 (실시간)")
                    .signaturePolicy(signaturePolicyLottecard)
                    .endpoint("http://localhost:$mockServerPort/banksalad/service-limit-info")
                    .method(Api.HttpMethod.POST)
                    .transform(
                        Api.request(
                            "transform/card/lottecard/plcc/header_req.jslt",
                            "transform/card/lottecard/plcc/reward_02-1_req.jslt"
                        ),
                        Api.response(
                            "transform/card/lottecard/plcc/header_res.jslt",
                            "transform/card/lottecard/plcc/reward_02-1_res.jslt"
                        )
                    ).build()
        }

        private val signaturePolicyLottecard = SignaturePolicy.builder()
            .algorithm(SignaturePolicy.Algorithm.NONE)
            .build()

        lateinit var card_lottecard_plcc_rewards: Api
    }
}
