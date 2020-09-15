package com.rainist.collectcard.common.execution

import com.rainist.collect.common.execution.Execution
import com.rainist.collectcard.card.dto.ListCardsResponse
import com.rainist.collectcard.cardbills.dto.ListBillTransactionsResponse
import com.rainist.collectcard.cardbills.dto.ListCardBillsResponse
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimitResponse
import com.rainist.collectcard.cardloans.dto.ListLoansResponse
import com.rainist.collectcard.cardloans.dto.Loan
import com.rainist.collectcard.cardtransactions.dto.ListTransactionsResponse
import com.rainist.collectcard.common.collect.api.ShinhancardApis

class MockExecutions {
    companion object {

        // 유효카드 정보조회 SHC_HPG00548
        val shinhancardCards =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_cards)
                .to(ListCardsResponse::class.java)
                .build()

        // 신용 국내사용내역조회-일시불/할부 SHC_HPG00428
        val shinhancardCreditDomesticTransactions =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_credit_domestic_transactions)
                .to(ListTransactionsResponse::class.java)
                .build()

        // 신용 해외사용내역조회-일시불조회 SHC_HPG01612
        val shinhancardCreditOverseaTransactions =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_credit_oversea_transactions)
                .to(ListTransactionsResponse::class.java)
                .build()

        // 체크 국내사용내역 조회 SHC_HPG01030
        val shinhancardCheckDomesticTransactions =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_check_domestic_transactions)
                .to(ListTransactionsResponse::class.java)
                .build()

        // 체크 해외사용내역 조회 SHC_HPG01031
        val shinhancardCheckOverseaTransactions =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_check_oversea_transactions)
                .to(ListTransactionsResponse::class.java)
                .build()

        // 카드_[EXT] 결제예정금액총괄 SHC_HPG01096_EXT
        val shinhancardBillsExpected =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_list_user_card_bills_expected)
                .to(ListCardBillsResponse::class.java)
                .build()

        // 카드_[EXT] 결제예정금액(일시불,현금서비스 상세) SHC_HPG00237
        val shinhancardBillsExpectedDetailLumpSum =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_lump_sum)
                .to(ListBillTransactionsResponse::class.java)
                .build()

        // (할부) 결제예정금액(할부, 론 상세) (SHC_HPG00238)
        val shinhancardBillsExpectedDetailInstallment =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_list_user_card_bills_expected_detail_installment)
                .to(ListBillTransactionsResponse::class.java)
                .build()

        // 체크카드 월별 청구내역(SHC_HPG01226)
        val shinhancardCheckBills =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_check_bills)
                .to(ListCardBillsResponse::class.java)
                .build()

        // 신용카드 월별 청구내역(SHC_HPG00719)
        val shinhancardCreditBills =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_credit_bills)
                .to(ListCardBillsResponse::class.java)
                .build()

        // 카드_[EXT] (체크) 월별청구내역조회(상세총괄) (SHC_HPG00537)
        val shinhancardcheckBillTransaction =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_check_bill_transactions)
                .to(ListBillTransactionsResponse::class.java)
                .build()

        // 카드_[EXT] (신용) 월별청구내역조회(상세총괄) (SHC_HPG00698)
        val shinhancardCreditBillTransaction =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_credit_bill_transactions)
                .to(ListBillTransactionsResponse::class.java)
                .build()

        // 대출 현황 정보 조회 (SHC_HPG00203)
        val shinhancardloanInfo =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_loan_info)
                .to(ListLoansResponse::class.java)
                .build()

        // 대출 상세 조회 (SHC_HPG00188)
        val shinhancardloanDetail =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_loan_detail)
                .to(Loan::class.java)
                .build()

        // 신용 한도조회 (SHC_HPG01730)
        val shinhancardCreditLimit =
            Execution.create()
                .exchange(ShinhancardApis.card_shinhancard_credit_limit)
                .to(CreditLimitResponse::class.java)
                .build()
    }
}
