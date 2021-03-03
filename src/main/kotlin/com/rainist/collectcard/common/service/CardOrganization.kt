package com.rainist.collectcard.common.service

data class CardOrganization(
    var code: String? = null,
    var name: String? = null,

    var clientId: String? = null,

    var organizationId: String? = null,
    var organizationObjectId: String? = null,

    var maxMonth: Int = DEFAULT_MAX_MONTH,
    var division: Int = DEFAULT_DIVISION,
    var researchInterval: Int = DEFAULT_RESEARCH_INTERVAL,

    // TODO PLCC 롯데카드로 부터 전달 받아서 사용
    var benefitProductCode: String = "",
    var screenInquiryCode: String = ""
) {
    companion object {
        var DEFAULT_MAX_MONTH = 12 // 초기 조회 최대 범위
        var DEFAULT_DIVISION = 3 // Async 분할 수
        var DEFAULT_RESEARCH_INTERVAL = 3 // 재검색 일자
    }
}
