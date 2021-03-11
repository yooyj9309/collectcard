package com.rainist.collectcard.plcc.cardtransactions.enums

enum class PlccCardServiceType(
    var index: Int,
    var description: String
) {

    REWARDS_SERVICE_TYPE_UNKNOWN(1, "알수없음"),
    REWARDS_SERVICE_TYPE_CHARGE_DISCOUNT(2, "청구할인"),
    REWARDS_SERVICE_TYPE_POINT(3, "포인트"),
    REWARDS_SERVICE_TYPE_INSTALLMENT_REDUCT(4, "할부감면")
    ;
}
