package com.rainist.collectcard.common.service

interface KeyManagementService {

    enum class KeyAlias {
        api_log, // API LOG
        card, // 카드
        card_bill, // 카드 청구서
        card_bill_transaction, // 카드 청구서 상세 내역
        card_loan, // 카드론 (대출)
        card_bill_scheduled, // 카드 결졔 예정 내역 청구서
        card_payment_scheduled, // 카드 결제 예정
        card_transaction // 카드 승인 상세 내역
    }

    fun getSecret(keyAlias: KeyAlias?): String?

    fun getIv(keyAlias: KeyAlias?): String?
}
