package com.rainist.collectcard.common.enums

enum class CardStatus(
    var index: Int,
    var description: String
) {

    REGISTERED(1, "등록상태"),
    TERMINATED(2, "종료된상태"),
    DORMANT(3, "휴먼상태"),
    SUSPENDED(4, "일시정지상태"),
    UNKNOWN(99, "알수없음")
    ;
}
