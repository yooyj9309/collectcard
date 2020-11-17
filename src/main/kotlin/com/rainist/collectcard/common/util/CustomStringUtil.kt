package com.rainist.collectcard.common.util

class CustomStringUtil {
    companion object {

        // 해당부분의 경우 신한카드 마스킹 앞자리가 4 ->6 으로 변하면서 card service에 내려줘도 안전한지 확인전까지 사용할 부분, 데이터 저장 중복 방지를 위해 사용
        fun replaceNumberToMask(cardNumber: String?): String {
            return cardNumber?.let {
                var builder = StringBuilder(it)
                if (builder.length >= 6) {
                    builder.setCharAt(4, '*')
                    builder.setCharAt(5, '*')
                }
                builder.toString()
            } ?: ""
        }
    }
}
