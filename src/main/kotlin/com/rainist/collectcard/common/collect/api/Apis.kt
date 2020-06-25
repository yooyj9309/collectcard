package com.rainist.collectcard.common.collect.api

import org.springframework.core.io.ClassPathResource

enum class BusinessType {
    card
}

enum class Organization {
    shinhancard,
}

enum class Transaction {
    cards, cardbills, cardTransaction, cardBillsExpected, billTransactions
}

class Apis {

    companion object {

        fun readText(fileInClassPath: String): String {
            val classPathResource = ClassPathResource(fileInClassPath)

            var inputStream = classPathResource.getInputStream()
            return inputStream.bufferedReader().use { it.readText() }
        }
    }
}
