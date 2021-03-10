package com.rainist.collectcard.common.collect.api

import org.springframework.core.io.ClassPathResource

enum class BusinessType {
    card, plcc
}

enum class Organization {
    shinhancard, lottecard
}

enum class Transaction {
    userInfo,
    cards,
    cardbills,
    cardTransaction,
    billTransactionExpected,
    loan,
    creditLimit,
    plccCardTransaction,
    plccCardReward
}

class Apis {

    companion object {

        fun readText(fileInClassPath: String): String {
            val classPathResource = ClassPathResource(fileInClassPath)

            val inputStream = classPathResource.getInputStream()

            val builder = StringBuilder()
            (inputStream.bufferedReader()).use {
                it.forEachLine { line ->
                    builder.append(if (line.contains("//")) line.substring(0, line.indexOf("//")) else line)
                }
            }

            return builder.toString()
        }
    }
}
