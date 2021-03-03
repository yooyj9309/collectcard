package com.rainist.collectcard.common.util

import com.rainist.collectcard.card.dto.Card
import com.rainist.collectcard.cardbills.dto.CardBill
import com.rainist.collectcard.cardbills.dto.CardBillTransaction
import com.rainist.collectcard.cardcreditlimit.dto.CreditLimit
import com.rainist.collectcard.cardloans.dto.Loan
import com.rainist.collectcard.cardtransactions.dto.CardTransaction
import java.lang.reflect.Field

class ReflectionCompareUtil {
    companion object {
        fun reflectionCompareBills(
            oldBills: List<CardBill>,
            newBills: List<CardBill>
        ): MutableMap<String, Any> {

            val diffFieldName = mutableListOf<String>()
            val diffFieldMap = mutableMapOf<String, Any>()

            // bill 비교
            for (index in oldBills.indices) {
                val declaredFields = oldBills[index].javaClass.declaredFields
                declaredFields.forEach { billField ->
                    billField.isAccessible = true
                    val oldBillField = billField.get(oldBills[index])
                    val newBillField = billField.get(newBills[index])

                    // 하나만 null일 경우만 map에 추가
                    if (oldBillField == null || newBillField == null) {
                        if (!(oldBillField == null && newBillField == null)) {
                            diffFieldName.add(billField.name)
                            if (billField.name != "transactions") {
                                diffFieldMap["oldBills.${index}th bill.${billField.name}"] = oldBillField ?: "null"
                                diffFieldMap["newBills.${index}th bill.${billField.name}"] = newBillField ?: "null"
                            }
                        }
                    } else if (!oldBillField.equals(newBillField)) {
                        diffFieldName.add(billField.name)
                        /**
                         * billTransactions 데이터만 빼고 로그에 추가.
                         * 이유는 billTransactions가 차이가 있다면 아래 reflectionCompareCardBillTransaction에서 로그가 추가된다. 중복방지.
                         */
                        if (billField.name != "transactions") {
                            diffFieldMap["oldBills.${index}th bill.${billField.name}"] = oldBillField.toString()
                            diffFieldMap["newBills.${index}th bill.${billField.name}"] = newBillField.toString()
                        }
                    }
                }
                // billTransaction이 다를 경우 비교
                if (diffFieldName.contains("transactions")) {
                    val oldBillTransactions = oldBills[index].transactions
                    val newBillTransactions = newBills[index].transactions

                    /**
                     * oldBillTransactions, newBillTransactions 둘 중 하나가 null인 경우가 있어서 double let 처리
                     */
                    oldBillTransactions?.let {
                        newBillTransactions?.let {
                            if (oldBillTransactions.size == newBillTransactions.size) {
                                return reflectionCompareCardBillTransaction(
                                    oldBillTransactions,
                                    newBillTransactions,
                                    diffFieldMap
                                )
                            }
                        }
                    }
                }
            }
            return diffFieldMap
        }

        private fun reflectionCompareCardBillTransaction(
            oldBillTransactions: MutableList<CardBillTransaction>,
            newBillTransactions: MutableList<CardBillTransaction>,
            diffFieldMap: MutableMap<String, Any>
        ): MutableMap<String, Any> {

            for (index in oldBillTransactions.indices) {
                val declaredFields = oldBillTransactions[index].javaClass.declaredFields
                declaredFields.forEach { billTransactionField ->
                    billTransactionField.isAccessible = true

                    val oldTransactionField = billTransactionField.get(oldBillTransactions[index])
                    val newTransactionField = billTransactionField.get(newBillTransactions[index])

                    if (oldTransactionField == null || newTransactionField == null) {
                        if (!(oldTransactionField == null && newTransactionField == null)) {
                            diffFieldMap["oldBillTransaction.${index}th.${billTransactionField.name}"] =
                                oldTransactionField ?: "null"
                            diffFieldMap["newBillTransaction.${index}th.${billTransactionField.name}"] =
                                newTransactionField ?: "null"
                        }
                    } else if (!oldTransactionField.equals(newTransactionField)) {
                        diffFieldMap["oldBillTransaction.${index}th.${billTransactionField.name}"] = oldTransactionField
                        diffFieldMap["newBillTransaction.${index}th.${billTransactionField.name}"] = newTransactionField
                    }
                }
            }
            return diffFieldMap
        }

        fun reflectionCompareCardTransaction(
            oldTransactions: List<CardTransaction>,
            newTransactions: List<CardTransaction>
        ): MutableMap<String, Any> {

            val diffFieldMap = mutableMapOf<String, Any>()

            // 각 DTO별로 비교
            for (index in oldTransactions.indices) {
                val old = oldTransactions[index]
                val new = newTransactions[index]

                val declaredFields = old.javaClass.declaredFields
                declaredFields.forEach { field ->
                    field.isAccessible = true
                    val oldField = field.get(old)
                    val newField = field.get(new)

                    addLogForDiffFields("cardTransaction", oldField, newField, diffFieldMap, index, field)
                }
            }
            return diffFieldMap
        }

        fun reflectionCompareCards(oldCards: List<Card>, newCards: List<Card>): MutableMap<String, Any> {

            val diffFieldMap = mutableMapOf<String, Any>()

            for (index in oldCards.indices) {
                val old = oldCards[index]
                val new = newCards[index]

                val declaredFields = old.javaClass.declaredFields
                declaredFields.forEach { field ->
                    field.isAccessible = true

                    val oldField = field.get(old)
                    val newField = field.get(new)

                    addLogForDiffFields("card", oldField, newField, diffFieldMap, index, field)
                }
            }
            return diffFieldMap
        }

        fun reflectionCompareCardLoans(oldLoans: List<Loan>, newLoans: List<Loan>): MutableMap<String, Any> {
            val diffFieldMap = mutableMapOf<String, Any>()

            for (index in oldLoans.indices) {
                val old = oldLoans[index]
                val new = newLoans[index]

                val declaredFields = old.javaClass.declaredFields
                declaredFields.forEach { field ->
                    field.isAccessible = true

                    val oldField = field.get(old)
                    val newField = field.get(new)

                    addLogForDiffFields("loan", oldField, newField, diffFieldMap, index, field)
                }
            }
            return diffFieldMap
        }

        fun reflectionCompareCreditLimit(
            oldCreditLimit: CreditLimit,
            newCreditLimit: CreditLimit
        ): MutableMap<String, Any> {

            val diffFieldMap = mutableMapOf<String, Any>()

            val declaredFields = oldCreditLimit.javaClass.declaredFields
            declaredFields.forEach { limit ->
                limit.isAccessible = true
                val old = limit.get(oldCreditLimit)
                val new = limit.get(newCreditLimit)

                /**
                 * oldLimit, newLimit 객체가 둘 다 null이 아닐 때 비교를 위해 double let
                 */
                old?.let {
                    new?.let {
                        val limitDeclaredFields = old.javaClass.declaredFields
                        limitDeclaredFields.forEach { field ->
                            field.isAccessible = true
                            val oldField = field.get(old)
                            val newField = field.get(new)

                            if (oldField == null || newField == null) {
                                if (!(oldField == null && newField == null)) {
                                    diffFieldMap["oldCreditLimit.${limit.name}.${field.name}"] = oldField ?: "null"
                                    diffFieldMap["newCreditLimit.${limit.name}.${field.name}"] = newField ?: "null"
                                }
                            } else if (!oldField.equals(newField)) {
                                diffFieldMap["oldCreditLimit.${limit.name}.${field.name}"] = oldField.toString()
                                diffFieldMap["newCreditLimit.${limit.name}.${field.name}"] = newField.toString()
                            }
                        }
                    }
                }
            }
            return diffFieldMap
        }

        private fun addLogForDiffFields(
            dtoName: String,
            oldField: Any?,
            newField: Any?,
            diffFieldMap: MutableMap<String, Any>,
            index: Int,
            field: Field
        ) {
            if (oldField == null || newField == null) {
                if (!(oldField == null && newField == null)) {
                    diffFieldMap["old$dtoName.${index}th.${field.name}"] = oldField ?: "null"
                    diffFieldMap["new$dtoName.${index}th.${field.name}"] = newField ?: "null"
                }
            } else if (!oldField.equals(newField)) {
                diffFieldMap["old$dtoName.${index}th.${field.name}"] = oldField.toString()
                diffFieldMap["new$dtoName.${index}th.${field.name}"] = newField.toString()
            }
        }
    }
}
