package com.rainist.collectcard.cardbills

import com.rainist.collectcard.cardbills.dto.BillCardType
import com.rainist.collectcard.cardbills.dto.CardBill
import com.rainist.collectcard.cardbills.dto.CardBillTransaction
import com.rainist.collectcard.cardbills.util.CardBillUtil
import com.rainist.collectcard.common.db.entity.CardBillEntity
import com.rainist.collectcard.common.db.entity.CardBillHistoryEntity
import com.rainist.collectcard.common.db.entity.CardBillTransactionEntity
import com.rainist.collectcard.common.db.entity.CardPaymentScheduledEntity
import com.rainist.collectcard.common.db.repository.CardBillHistoryRepository
import com.rainist.collectcard.common.db.repository.CardBillRepository
import com.rainist.collectcard.common.db.repository.CardBillTransactionRepository
import com.rainist.collectcard.common.db.repository.CardPaymentScheduledRepository
import com.rainist.collectcard.common.enums.CardTransactionType
import com.rainist.collectcard.common.enums.CardType
import java.math.BigDecimal
import java.time.LocalDateTime
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@DisplayName("청구서 repository 테스트")
class CardBillRepositoryTest {

    @Autowired
    lateinit var cardBillRepository: CardBillRepository

    @Autowired
    lateinit var cardBillHistoryRepository: CardBillHistoryRepository

    @Autowired
    lateinit var cardBillTransactionRepository: CardBillTransactionRepository

    @Autowired
    lateinit var cardPaymentScheduledRepository: CardPaymentScheduledRepository

    @Test
    @Transactional
    @Rollback
    fun cardBillEntityTest() {
        val cardBill = makeMockCardBill()
        val now = LocalDateTime.now()
        val cardBillEntity = CardBillUtil.makeCardBillEntity(1, "shinhancard", cardBill, now)

        assertThat(cardBillEntity).isEqualToComparingFieldByField(CardBillEntity().apply {
            this.banksaladUserId = 1
            this.cardCompanyId = "shinhancard"
            this.billNumber = "202008140002"
            this.billType = "0002"
            this.cardType = BillCardType.CREDIT.name
            this.lastCheckAt = now
            this.userName = "김뱅샐"
            this.userGrade = "testMaster"
            this.userGradeOrigin = "1"
            this.paymentDay = "20200801"
            this.billedYearMonth = "202008"
            this.nextPaymentDay = "202009"
            this.billingAmount = BigDecimal("100000.0000")
            this.prepaidAmount = BigDecimal("100000.0000")
            this.paymentBankId = "088"
            this.paymentAccountNumber = "10101******0"
            this.totalPoint = BigDecimal("5.0000")
            this.expiringPoints = BigDecimal("5.0000")
        })

        val findEntity = cardBillRepository.save(cardBillEntity)
        Assertions.assertNotNull(findEntity)
    }

    @Test
    @Transactional
    @Rollback
    fun cardBillHistoryEntityTest() {
        val cardBill = makeMockCardBill()
        val now = LocalDateTime.now()
        var cardBillId: Long = 0
        val cardBillHistoryEntity = CardBillUtil.makeCardBillEntity(1, "shinhancard", cardBill, now).let {
            val cardBillEntity = cardBillRepository.save(it)
            cardBillId = cardBillEntity.cardBillId ?: 0
            CardBillUtil.makeCardBillHistoryEntityFromCardBillHistory(cardBillEntity)
        }

        assertThat(cardBillHistoryEntity).isEqualToComparingFieldByField(CardBillHistoryEntity().apply {
            this.cardBillId = cardBillId
            this.banksaladUserId = 1
            this.cardCompanyId = "shinhancard"
            this.billNumber = "202008140002"
            this.billType = "0002"
            this.cardType = BillCardType.CREDIT.name
            this.lastCheckAt = now
            this.userName = "김뱅샐"
            this.userGrade = "testMaster"
            this.userGradeOrigin = "1"
            this.paymentDay = "20200801"
            this.billedYearMonth = "202008"
            this.nextPaymentDay = "202009"
            this.billingAmount = BigDecimal("100000.0000")
            this.prepaidAmount = BigDecimal("100000.0000")
            this.paymentBankId = "088"
            this.paymentAccountNumber = "10101******0"
            this.totalPoint = BigDecimal("5.0000")
            this.expiringPoints = BigDecimal("5.0000")
        })

        val findHistoryEntity = cardBillHistoryRepository.save(cardBillHistoryEntity)
        Assertions.assertNotNull(findHistoryEntity)
    }

    @Test
    @Transactional
    @Rollback
    fun cardBillTransactionTest() {
        val cardBillTransaction = makeMockCardBillTransaction()
        val now = LocalDateTime.now()
        val cardBillTransactionEntity =
            CardBillUtil.makeCardBillTransactionEntity(1, "shinhancard", "202008", 1, cardBillTransaction, now)

        assertThat(cardBillTransactionEntity).isEqualToComparingFieldByField(CardBillTransactionEntity().apply {
            this.banksaladUserId = 1
            this.billedYearMonth = "202008"
            this.lastCheckAt = now
            this.cardCompanyId = "shinhancard"
            this.cardBillTransactionNo = 1
            this.cardCompanyCardId = "testCardId"
            this.cardName = "test"
            this.cardNumber = "123456789"
            this.cardNumberMask = "123456789"
            this.businessLicenseNumber = "123456789"
            this.storeName = "store"
            this.storeNumber = "123456789"
            this.cardType = CardType.DEBIT.name
            this.cardTypeOrigin = "1"
            this.cardTransactionType = CardTransactionType.APPROVAL.name
            this.cardTransactionTypeOrigin = "1"
            this.currencyCode = "KRW"
            this.isInstallmentPayment = true
            this.installment = 1
            this.installmentRound = 1
            this.amount = BigDecimal("10000.0000")
            this.netSalesAmount = BigDecimal("10000.0000")
            this.serviceChargeAmount = BigDecimal("10000.0000")
            this.taxAmount = BigDecimal("10000.0000")
            this.paidPoints = BigDecimal("10000.0000")
            this.isPointPay = false
            this.discountAmount = BigDecimal("10000.0000")
            this.canceledAmount = BigDecimal("10000.0000")
            this.approvalNumber = "123456789"
            this.approvalDay = "20200801"
            this.approvalTime = "595959"
            this.pointsToEarn = BigDecimal("10000.0000")
            this.isOverseaUse = false
            this.paymentDay = "20200801"
            this.storeCategory = "type"
            this.storeCategoryOrigin = "typeOrigin"
            this.transactionCountry = "country"
            this.billingRound = 1
            this.paidAmount = BigDecimal("10000.0000")
            this.billedAmount = BigDecimal("10000.0000")
            this.billedFee = BigDecimal("10000.0000")
            this.remainingAmount = BigDecimal("10000.0000")
            this.isPaidFull = false
            this.cashbackAmount = BigDecimal("10000.0000")
            this.pointsRate = BigDecimal("10000.0000")
            this.billNumber = "123456789"
            this.billType = "123456789"
        })

        val findTransactionEntity = cardBillTransactionRepository.save(cardBillTransactionEntity)
        Assertions.assertNotNull(findTransactionEntity)
    }

    @Test
    @Transactional
    @Rollback
    fun cardPaymentScheduledEntityTest() {
        val cardBillTransaction = makeMockCardBillTransaction()
        val now = LocalDateTime.now()
        val paymentScheduledEntity =
            CardBillUtil.makeCardPaymentScheduledEntity(1, "shinhancard", 1, cardBillTransaction, now)

        assertThat(paymentScheduledEntity).isEqualToComparingFieldByField(CardPaymentScheduledEntity().apply {
            this.banksaladUserId = 1
            this.paymentScheduledTransactionNo = 1
            this.lastCheckAt = now
            this.cardCompanyId = "shinhancard"
            this.cardCompanyCardId = "testCardId"
            this.cardName = "test"
            this.cardNumber = "123456789"
            this.cardNumberMask = "123456789"
            this.businessLicenseNumber = "123456789"
            this.storeName = "store"
            this.storeNumber = "123456789"
            this.cardType = CardType.DEBIT.name
            this.cardTypeOrigin = "1"
            this.cardTransactionType = CardTransactionType.APPROVAL.name
            this.cardTransactionTypeOrigin = "1"
            this.currencyCode = "KRW"
            this.isInstallmentPayment = true
            this.installment = 1
            this.installmentRound = 1
            this.amount = BigDecimal("10000.0000")
            this.netSalesAmount = BigDecimal("10000.0000")
            this.serviceChargeAmount = BigDecimal("10000.0000")
            this.taxAmount = BigDecimal("10000.0000")
            this.paidPoints = BigDecimal("10000.0000")
            this.isPointPay = false
            this.discountAmount = BigDecimal("10000.0000")
            this.canceledAmount = BigDecimal("10000.0000")
            this.approvalNumber = "123456789"
            this.approvalDay = "20200801"
            this.approvalTime = "595959"
            this.pointsToEarn = BigDecimal("10000.0000")
            this.isOverseaUse = false
            this.paymentDay = "20200801"
            this.storeCategory = "type"
            this.storeCategoryOrigin = "typeOrigin"
            this.transactionCountry = "country"
            this.billingRound = 1
            this.paidAmount = BigDecimal("10000.0000")
            this.billedAmount = BigDecimal("10000.0000")
            this.billedFee = BigDecimal("10000.0000")
            this.remainingAmount = BigDecimal("10000.0000")
            this.isPaidFull = false
            this.cashbackAmount = BigDecimal("10000.0000")
            this.pointsRate = BigDecimal("10000.0000")
            this.billNumber = "123456789"
            this.billType = "123456789"
        })

        val findPaymentScheduledEntity = cardPaymentScheduledRepository.save(paymentScheduledEntity)
        Assertions.assertNotNull(findPaymentScheduledEntity)
    }

    private fun makeMockCardBill(): CardBill {
        return CardBill(
            null,
            "202008140002",
            "0002",
            BillCardType.CREDIT,
            "김뱅샐",
            "testMaster",
            "1",
            "20200801",
            "202008",
            "202009",
            BigDecimal("100000"),
            BigDecimal("100000"),
            "088",
            "10101******0",
            5,
            5,
            null,
            null
        )
    }

    private fun makeMockCardBillTransaction(): CardBillTransaction {
        return CardBillTransaction().apply {
            this.cardTransactionId = "1"
            this.cardCompanyCardId = "testCardId"
            this.cardName = "test"
            this.cardNumber = "123456789"
            this.cardNumberMasked = "123456789"
            this.businessLicenseNumber = "123456789"
            this.storeName = "store"
            this.storeNumber = "123456789"
            this.cardType = CardType.DEBIT
            this.cardTypeOrigin = "1"
            this.cardTransactionType = CardTransactionType.APPROVAL
            this.cardTransactionTypeOrigin = "1"
            this.currencyCode = "KRW"
            this.isInstallmentPayment = true
            this.installment = 1
            this.installmentRound = 1
            this.netSalesAmount = BigDecimal(10000)
            this.serviceChargeAmount = BigDecimal(10000)
            this.tax = BigDecimal(10000)
            this.paidPoints = BigDecimal(10000)
            this.isPointPay = false
            this.discountAmount = BigDecimal(10000)
            this.amount = BigDecimal(10000)
            this.canceledAmount = BigDecimal(10000)
            this.approvalNumber = "123456789"
            this.approvalDay = "20200801"
            this.approvalTime = "595959"
            this.pointsToEarn = BigDecimal(10000)
            this.isOverseaUse = false
            this.paymentDay = "20200801"
            this.storeCategory = "type"
            this.storeCategoryOrigin = "typeOrigin"
            this.transactionCountry = "country"
            this.billingRound = 1
            this.paidAmount = BigDecimal(10000)
            this.billedAmount = BigDecimal(10000)
            this.billedFee = BigDecimal(10000)
            this.remainingAmount = BigDecimal(10000)
            this.isPaidFull = false
            this.cashback = BigDecimal(10000)
            this.pointsRate = BigDecimal(10000)
            this.billNumber = "123456789"
            this.billType = "123456789"
        }
    }
}
