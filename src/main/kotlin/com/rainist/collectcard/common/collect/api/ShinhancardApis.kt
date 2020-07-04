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

        // 카드_[EXT] 결제예정금액 총괄 SHC_HPG01096_EXT
        card_shinhancard_list_user_card_bills_expected =
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.billTransactionExpected.name)
                .signaturePolicy(signaturePolicyShinhancard)
                .endpoint("$hostCardShinhancard/v1.0/EXT/usecard/searchtotalpayments")
                .method(Api.HttpMethod.POST)
                .transformRequestHeader(readText("transform/card/shinhancard/header_req.jslt"))
                .transformRequestBody(readText("transform/card/shinhancard/billTransactionExpected_SHC_HPG01096_req.jslt"))
                .transformResponseHeader(readText("transform/card/shinhancard/header_res.jslt"))
                .transformResponseBody(readText("transform/card/shinhancard/billTransactionExpected_SHC_HPG01096_res.jslt"))
                .name("카드_[EXT] 결제예정금액 총괄(SHC_HPG01096_EXT)")
                .build()

        // 결제예정금액(일시불,현금서비스 상세) (SHC_HPG00237)
        card_shinhancard_list_user_card_bills_expected_detail_lump_sum =
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.billTransactionExpectedDetailsLumpSum.name)
                .signaturePolicy(signaturePolicyShinhancard)
                .endpoint("$hostCardShinhancard/v1.0/EXT/usecard/searchpaymentsdetail")
                .method(Api.HttpMethod.POST)
                .transformRequestHeader(readText("transform/card/shinhancard/header_req.jslt"))
                .transformRequestBody(readText("transform/card/shinhancard/billTransactionExpectedDetailsLumpSum_SHC_HPG00237_req.jslt"))
                .transformResponseHeader(readText("transform/card/shinhancard/header_res.jslt"))
                .transformResponseBody(readText("transform/card/shinhancard/billTransactionExpectedDetailsLumpSum_SHC_HPG00237_res.jslt"))
                .name("카드_[EXT] 결제예정금액(일시불,현금서비스 상세)(SHC_HPG00237)")
                .build()

        // (할부) 결제예정금액(할부, 론 상세) (SHC_HPG00238)
        card_shinhancard_list_user_card_bills_expected_detail_installment =
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.billTransactionExpectedDetailsInstallment.name)
                .signaturePolicy(signaturePolicyShinhancard)
                .endpoint("$hostCardShinhancard/v1.0/EXT/useinstallment/searchpaymentsdetail")
                .method(Api.HttpMethod.POST)
                .transformRequestHeader(readText("transform/card/shinhancard/header_req.jslt"))
                .transformRequestBody(readText("transform/card/shinhancard/billTransactionExpectedDetailsInstallment_SHC_HPG00238_req.jslt"))
                .transformResponseHeader(readText("transform/card/shinhancard/header_res.jslt"))
                .transformResponseBody(readText("transform/card/shinhancard/billTransactionExpectedDetailsInstallment_SHC_HPG00238_req.jslt"))
                .name("카드_[EXT] (할부) 결제예정금액(할부, 론 상세)(SHC_HPG00238)")
                .build()

        // 체크카드 월별 청구내역 최근 6개월 이력 조회 (SHC_HPG01226)
        card_shinhancard_check_bills =
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.cardbills.name)
                .signaturePolicy(signaturePolicyShinhancard)
                .endpoint("$hostCardShinhancard/v1.0/EXT/usedebitcard/searchmonthlybillingfor6months")
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
                .endpoint("$hostCardShinhancard/v1.0/EXT/usecreditcard/searchmonthlybillingfor6months")
                .method(Api.HttpMethod.POST)
                .transformRequestHeader(readText("transform/card/shinhancard/header_req.jslt"))
                .transformRequestBody(readText("transform/card/shinhancard/cardbill_SHC_HPG00719_req.jslt"))
                .transformResponseHeader(readText("transform/card/shinhancard/header_res.jslt"))
                .transformResponseBody(readText("transform/card/shinhancard/cardbill_SHC_HPG00719_res.jslt"))
                .name("신용카드 월별 청구내역(SHC_HPG00719)")
                .build()

        // 체크카드 월별 청구내역(SHC_HPG00537)
        card_shinhancard_check_bill_transactions =
        Api.builder()
            .business(BusinessType.card.name)
            .agency(Organization.shinhancard.name)
            .transaction(Transaction.cardbills.name)
            .signaturePolicy(signaturePolicyShinhancard)
            .endpoint("$hostCardShinhancard/v1.0/EXT/usedebitcard/searchmonthlybillingdetail")
            .method(Api.HttpMethod.POST)
            .transformRequestHeader(readText("transform/card/shinhancard/header_req.jslt"))
            .transformRequestBody(readText("transform/card/shinhancard/billtransaction_SHC_HPG00537_req.jslt"))
            .transformResponseHeader(readText("transform/card/shinhancard/header_res.jslt"))
            .transformResponseBody(readText("transform/card/shinhancard/billtransaction_SHC_HPG00537_res.jslt"))
            .name("체크카드 월별 청구내역 상세(SHC_HPG00537)")
            .build()

        // 체크카드 월별 청구내역(SHC_HPG00698)
        card_shinhancard_credit_bill_transactions =
        Api.builder()
            .business(BusinessType.card.name)
            .agency(Organization.shinhancard.name)
            .transaction(Transaction.cardbills.name)
            .signaturePolicy(signaturePolicyShinhancard)
            .endpoint("$hostCardShinhancard/v1.0/EXT/usercreditcard/searchmonthlybillingdetail")
            .method(Api.HttpMethod.POST)
            .transformRequestHeader(readText("transform/card/shinhancard/header_req.jslt"))
            .transformRequestBody(readText("transform/card/shinhancard/billtransaction_SHC_HPG00698_req.jslt"))
            .transformResponseHeader(readText("transform/card/shinhancard/header_res.jslt"))
            .transformResponseBody(readText("transform/card/shinhancard/billtransaction_SHC_HPG00698_res.jslt"))
            .name("신용카드 월별 청구내역 상세(SHC_HPG00698)")
            .build()

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

        // 대출정보 현황 정보 조회 (SHC_HPG00203)
        card_shinhancard_loan_info =
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.loan.name)
                .signaturePolicy(signaturePolicyShinhancard)
                .endpoint("$hostCardShinhancard/v1.0/EXT/uselongloan/searchproduct")
                .method(Api.HttpMethod.POST)
                .transformRequestHeader(readText("transform/card/shinhancard/header_req.jslt"))
                .transformRequestBody(readText("transform/card/shinhancard/loan_SHC_HPG00203_req.jslt"))
                .transformResponseHeader(readText("transform/card/shinhancard/header_res.jslt"))
                .transformResponseBody(readText("transform/card/shinhancard/loan_SHC_HPG00203_res.jslt"))
                .name("대출정보 현황 정보 조회 (SHC_HPG00203)")
                .build()

        // 대출 상세 정보 조회 (SHC_HPG00188)
        card_shinhancard_loan_detail =
            Api.builder()
                .business(BusinessType.card.name)
                .agency(Organization.shinhancard.name)
                .transaction(Transaction.loan.name)
                .signaturePolicy(signaturePolicyShinhancard)
                .endpoint("$hostCardShinhancard/v1.0/EXT/uselongloan/loancondition")
                .method(Api.HttpMethod.POST)
                .transformRequestHeader(readText("transform/card/shinhancard/header_req.jslt"))
                .transformRequestBody(readText("transform/card/shinhancard/loan_SHC_HPG00188_req.jslt"))
                .transformResponseHeader(readText("transform/card/shinhancard/header_res.jslt"))
                .transformResponseBody(readText("transform/card/shinhancard/loan_SHC_HPG00188_req.jslt"))
                .name("대출 상세 정보 조회 (SHC_HPG00188)")
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

        // 카드_[EXT] 결제예정금액(일시불,현금서비스 상세) SHC_HPG00237
        lateinit var card_shinhancard_list_user_card_bills_expected_detail_lump_sum: Api

        // (할부) 결제예정금액(할부, 론 상세) (SHC_HPG00238)
        lateinit var card_shinhancard_list_user_card_bills_expected_detail_installment: Api

        // 체크카드 월별 청구내역(SHC_HPG01226)
        lateinit var card_shinhancard_check_bills: Api

        // 신용카드 월별 청구내역(SHC_HPG00719)
        lateinit var card_shinhancard_credit_bills: Api

        // 체크카드 월별 청구내역(SHC_HPG00537)
        lateinit var card_shinhancard_check_bill_transactions: Api

        // 체크카드 월별 청구내역(SHC_HPG00698)
        lateinit var card_shinhancard_credit_bill_transactions: Api

        // 개인 거래 정보 조회 (SHC_EXT_00001)
        lateinit var card_shinhancard_user_info: Api

        // 대출 현황 정보 조회 (SHC_HPG00203)
        lateinit var card_shinhancard_loan_info: Api

        // 대출 상세 조회 (SHC_HPG00188)
        lateinit var card_shinhancard_loan_detail: Api
    }
}
