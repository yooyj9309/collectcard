package com.rainist.collectcard.common.collect.api

import org.springframework.util.ResourceUtils

enum class BusinessType {
    card
}

enum class Organization {
    shinhancard,
}

enum class Transaction {
    cards, cardbills, cardTransaction, cardBillsExpected
}

class Apis {

    companion object {

        fun readText(fileInClassPath: String): String {
            return ResourceUtils.getFile(fileInClassPath).readText(Charsets.UTF_8)
        }
    }
}
