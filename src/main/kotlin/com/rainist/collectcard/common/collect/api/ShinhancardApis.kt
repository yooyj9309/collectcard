package com.rainist.collectcard.common.collect.api

import com.rainist.collect.common.dto.Api
import com.rainist.collect.common.dto.SignaturePolicy
import com.rainist.collectcard.common.collect.api.Apis.Companion.readText
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ShinhancardApis {
    @Value("\${host.card.shinhancard}")
    lateinit var hostCardShinhancard: String

    @PostConstruct
    fun init() {
        // 유효카드 정보조회 SHC_HPG00548
        card_shinhancard_cards =
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.cards.name)
                .signaturePolicy(signaturePolicyShinhancard)
                .endpoint("$hostCardShinhancard/v1.0/EXT/mycard/searchavailablecard")
                .method(Api.HttpMethod.POST)
                .transformRequestHeader(readText("transform/card/shinhancard/header_req.jslt"))
                .transformRequestBody(readText("transform/card/shinhancard/cards_req.jslt"))
                .transformResponseHeader(readText("transform/card/shinhancard/header_res.jslt"))
                .transformResponseBody(readText("transform/card/shinhancard/cards_res.jslt"))
                .name("보유카드조회")
                .build()

        // 신용 국내사용내역조회-일시불/할부 SHC_HPG00428
        card_shinhancard_credit_domestic_transactions =
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.cardTransaction.name)
                .signaturePolicy(signaturePolicyShinhancard)
                .endpoint("$hostCardShinhancard/v1.0/EXT/usecreditcard/searchusefordomestic")
                .method(Api.HttpMethod.POST)
                .transformRequestHeader(readText("transform/card/shinhancard/header_req.jslt"))
                .transformRequestBody(readText("transform/card/shinhancard/transaction_SHC_HPG00428_req.jslt"))
                .transformResponseHeader(readText("transform/card/shinhancard/header_res.jslt"))
                .transformResponseBody(readText("transform/card/shinhancard/transaction_SHC_HPG00428_res.jslt"))
                .name("신용 국내사용내역 조회-일시불/할부(SHC_HPG00428)")
                .build()

        // 신용 해외사용내역조회-일시불조회 SHC_HPG01612
        card_shinhancard_credit_oversea_transactions =
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.cardTransaction.name)
                .signaturePolicy(signaturePolicyShinhancard)
                .endpoint("$hostCardShinhancard/v1.0/EXT/usecreditcard/searchuseforoverseas")
                .method(Api.HttpMethod.POST)
                .transformRequestHeader(readText("transform/card/shinhancard/header_req.jslt"))
                .transformRequestBody(readText("transform/card/shinhancard/transaction_SHC_HPG01612_req.jslt"))
                .transformResponseHeader(readText("transform/card/shinhancard/header_res.jslt"))
                .transformResponseBody(readText("transform/card/shinhancard/transaction_SHC_HPG01612_res.jslt"))
                .name("신용 해외사용내역조회-일시불조회(SHC_HPG01612)")
                .build()

        // 체크 국내사용내역 조회 SHC_HPG01030
        card_shinhancard_check_domestic_transactions =
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.cardTransaction.name)
                .signaturePolicy(signaturePolicyShinhancard)
                .endpoint("$hostCardShinhancard/v1.0/EXT/usedebitcard/searchusefordomestic")
                .method(Api.HttpMethod.POST)
                .transformRequestHeader(readText("transform/card/shinhancard/header_req.jslt"))
                .transformRequestBody(readText("transform/card/shinhancard/transaction_SHC_HPG01030_req.jslt"))
                .transformResponseHeader(readText("transform/card/shinhancard/header_res.jslt"))
                .transformResponseBody(readText("transform/card/shinhancard/transaction_SHC_HPG01030_res.jslt"))
                .name("체크 국내사용내역(SHC_HPG01030)")
                .build()

        // 체크 해외사용내역 조회 SHC_HPG01031
        card_shinhancard_check_oversea_transactions =
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.cardTransaction.name)
                .signaturePolicy(signaturePolicyShinhancard)
                .endpoint("$hostCardShinhancard/v1.0/EXT/usedebitcard/searchuseforoverseas")
                .method(Api.HttpMethod.POST)
                .transformRequestHeader(readText("transform/card/shinhancard/header_req.jslt"))
                .transformRequestBody(readText("transform/card/shinhancard/transaction_SHC_HPG01031_req.jslt"))
                .transformResponseHeader(readText("transform/card/shinhancard/header_res.jslt"))
                .transformResponseBody(readText("transform/card/shinhancard/transaction_SHC_HPG01031_res.jslt"))
                .name("체크 국내사용내역(SHC_HPG01031)")
                .build()

        card_shinhancard_list_user_card_bills_expected =
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.cardBillsExpected.name)
                .signaturePolicy(signaturePolicyShinhancard)
                .endpoint("$hostCardShinhancard/v1.0/EXT/usecard/searchtotalpayments")
                .method(Api.HttpMethod.POST)
                .transformRequestHeader(readText("transform/card/shinhancard/header_req.jslt"))
                .transformRequestBody(readText("transform/card/shinhancard/cardbilltransactions_SHC_HPG01096_req.jslt"))
                .transformResponseHeader(readText("transform/card/shinhancard/header_res.jslt"))
                .transformResponseBody(readText("transform/card/shinhancard/cardbilltransactions_SHC_HPG01096_res.jslt"))
                .name("카드_[EXT] 결제예정금액 총괄(SHC_HPG01096_EXT)")
                .build()

        // 체크카드 월별 청구내역(SHC_HPG01226)
        card_shinhancard_check_bills =
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.cardbills.name)
                .signaturePolicy(signaturePolicyShinhancard)
                .endpoint("$hostCardShinhancard/v1.0/EXT/userdebitcard/searchmonthlybillingfor6months")
                .method(Api.HttpMethod.POST)
                .transformRequestHeader(readText("transform/card/shinhancard/header_req.jslt"))
                .transformRequestBody(readText("transform/card/shinhancard/cardbill_SHC_HPG01226_req.jslt"))
                .transformResponseHeader(readText("transform/card/shinhancard/header_res.jslt"))
                .transformResponseBody(readText("transform/card/shinhancard/cardbill_SHC_HPG01226_res.jslt"))
                .name("체크카드 월별 청구내역(SHC_HPG01226)")
                .build()

        // 신용카드 월별 청구내역(SHC_HPG00719)
        card_shinhancard_credit_bills =
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.cardbills.name)
                .signaturePolicy(signaturePolicyShinhancard)
                .endpoint("$hostCardShinhancard/v1.0/EXT/usercreditcard/searchmonthlybillingfor6months")
                .method(Api.HttpMethod.POST)
                .transformRequestHeader(readText("transform/card/shinhancard/header_req.jslt"))
                .transformRequestBody(readText("transform/card/shinhancard/cardbill_SHC_HPG00719_req.jslt"))
                .transformResponseHeader(readText("transform/card/shinhancard/header_res.jslt"))
                .transformResponseBody(readText("transform/card/shinhancard/cardbill_SHC_HPG00719_res.jslt"))
                .name("신용카드 월별 청구내역(SHC_HPG00719)")
                .build()

// TODO(sangmin) card bill transactions 구현 후 주석 해제
//
//        val card_shinhancard_check_bill_transactions: Api =
//            Api.builder()
//                .business(BusinessType.card.name)
//                .agency(Organization.shinhancard.name)
//                .transaction(Transaction.cardbills.name)
//                .signaturePolicy(signaturePolicyShinhancard)
//                .endpoint("$hostCardShinhancard/v1.0/EXT/userdebitcard/searchmonthlybillingdetail")
//                .method(Api.HttpMethod.POST)
//                .transformRequestHeader(readText("transform/card/shinhancard/header_req.jslt"))
//                .transformRequestBody(readText("transform/card/shinhancard/.jslt"))
//                .transformResponseHeader(readText("transform/card/shinhancard/header_res.jslt"))
//                .transformResponseBody(readText("transform/card/shinhancard/.jslt"))
//                .name("체크카드 월별 청구내역 상세(SHC_HPG00537)")
//                .build()
//
//        val card_shinhancard_credit_bill_transactions: Api =
//            Api.builder()
//                .business(BusinessType.card.name)
//                .agency(Organization.shinhancard.name)
//                .transaction(Transaction.cardbills.name)
//                .signaturePolicy(signaturePolicyShinhancard)
//                .endpoint("$hostCardShinhancard/v1.0/EXT/usercreditcard/searchmonthlybillingdetail")
//                .method(Api.HttpMethod.POST)
//                .transformRequestHeader(readText("transform/card/shinhancard/header_req.jslt"))
//                .transformRequestBody(readText("transform/card/shinhancard/.jslt"))
//                .transformResponseHeader(readText("transform/card/shinhancard/header_res.jslt"))
//                .transformResponseBody(readText("transform/card/shinhancard/.jslt"))
//                .name("신용카드 월별 청구내역 상세(SHC_HPG00698)")
//                .build()

        // 사용자 거래정보 조회 (SHC_EXT_00001)
        card_shinhancard_user_info =
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.userInfo.name)
                .signaturePolicy(signaturePolicyShinhancard)
                .endpoint("$hostCardShinhancard/v1.0/EXT/myinfo/searchtransinfo")
                .method(Api.HttpMethod.POST)
                .transformRequestHeader(readText("transform/card/shinhancard/header_req.jslt"))
                .transformRequestBody(readText("transform/card/shinhancard/userinfo_SHC_EXT00001_req.jslt"))
                .transformResponseHeader(readText("transform/card/shinhancard/header_res.jslt"))
                .transformResponseBody(readText("transform/card/shinhancard/userinfo_SHC_EXT00001_res.jslt"))
                .name("사용자 거래정보 조회 (SHC_EXT_00001)")
                .build()
    }

    companion object {
        private val signaturePolicyShinhancard = SignaturePolicy.builder()
            .algorithm(SignaturePolicy.Algorithm.NONE)
            .build()

        // 유효카드 정보조회 SHC_HPG00548
        lateinit var card_shinhancard_cards: Api

        // 신용 국내사용내역조회-일시불/할부 SHC_HPG00428
        lateinit var card_shinhancard_credit_domestic_transactions: Api

        // 신용 해외사용내역조회-일시불조회 SHC_HPG01612
        lateinit var card_shinhancard_credit_oversea_transactions: Api

        // 체크 국내사용내역 조회 SHC_HPG01030
        lateinit var card_shinhancard_check_domestic_transactions: Api

        // 체크 해외사용내역 조회 SHC_HPG01031
        lateinit var card_shinhancard_check_oversea_transactions: Api

        // 카드_[EXT] 결제예정금액총괄 SHC_HPG01096_EXT
        lateinit var card_shinhancard_list_user_card_bills_expected: Api

        // 체크카드 월별 청구내역(SHC_HPG01226)
        lateinit var card_shinhancard_check_bills: Api

        // 신용카드 월별 청구내역(SHC_HPG00719)
        lateinit var card_shinhancard_credit_bills: Api

        // 개인 거래 정보 조회 (SHC_EXT_00001)
        lateinit var card_shinhancard_user_info: Api
    }
}
