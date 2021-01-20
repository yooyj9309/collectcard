package com.rainist.collectcard.common.service

import com.rainist.collectcard.common.crypto.Base64Util
import com.rainist.collectcard.common.exception.CollectcardException
import com.rainist.collectcard.grpc.client.CipherClientService
import com.rainist.common.log.Log
import java.util.Base64
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kms.KmsClient
import software.amazon.awssdk.services.kms.model.DecryptRequest

@Service
class KeyManagementServiceImpl(
    val cipherClientService: CipherClientService
) : KeyManagementService {

    enum class TableNameForCipher {
        api_log, // API LOG
        card, // 카드
        card_bill, // 카드 청구서
        card_bill_transaction, // 카드 청구서 상세 내역
        card_loan, // 카드론 (대출)
        card_bill_scheduled, // 카드 결제 예정 내역 청구서
        card_payment_scheduled, // 카드 결제 예정
        card_transaction // 카드 승인 상세 내역
    }

    companion object : Log {
        val COLLECTCARD_DB_NAME = "collectcard"
    }

    @Value("\${aws.region}")
    private val awsRegion: String? = null

    @Value("\${aws.iam.collectcard.access-key}")
    private val awsAccessKey: String? = null

    @Value("\${aws.iam.collectcard.access-token}")
    private val awsAccessToken: String? = null

    @Value("\${cipher.iv.api_log}")
    private val apiLogIv: String? = null

    @Value("\${cipher.iv.card}")
    private val cardIv: String? = null

    @Value("\${cipher.iv.card_bill}")
    private val cardBillIv: String? = null

    @Value("\${cipher.iv.card_bill_transaction}")
    private val cardBillTransactionIv: String? = null

    @Value("\${cipher.iv.card_loan}")
    private val cardLoanIv: String? = null

    @Value("\${cipher.iv.card_bill_scheduled}")
    private val cardBillScheduledIv: String? = null

    @Value("\${cipher.iv.card_payment_scheduled}")
    private val cardPaymentScheduledIv: String? = null

    @Value("\${cipher.iv.card_transaction}")
    private val cardTransactionIv: String? = null

    private var apiLogSecret: String? = null
    private var cardSecret: String? = null
    private var cardBillSecret: String? = null
    private var cardBillTransactionSecret: String? = null
    private var cardLoanSecret: String? = null
    private var cardBillScheduledSecret: String? = null
    private var cardPaymentScheduledSecret: String? = null
    private var cardTransactionSecret: String? = null

    private var kmsClient: KmsClient? = null

    @PostConstruct
    private fun init() {
        kmsClient = KmsClient.builder()
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(awsAccessKey, awsAccessToken)))
            .region(Region.of(awsRegion))
            .build()

        apiLogSecret = decryptSecret(
            cipherClientService.getEncryptedDbTableCipherKey(COLLECTCARD_DB_NAME, TableNameForCipher.api_log.name)?.cipherKey
        )

        cardSecret = decryptSecret(
            cipherClientService.getEncryptedDbTableCipherKey(COLLECTCARD_DB_NAME, TableNameForCipher.card.name)?.cipherKey
        )

        cardBillSecret = decryptSecret(
            cipherClientService.getEncryptedDbTableCipherKey(COLLECTCARD_DB_NAME, TableNameForCipher.card_bill.name)?.cipherKey
        )

        cardBillTransactionSecret = decryptSecret(
            cipherClientService.getEncryptedDbTableCipherKey(COLLECTCARD_DB_NAME, TableNameForCipher.card_bill_transaction.name)?.cipherKey
        )

        cardLoanSecret = decryptSecret(
            cipherClientService.getEncryptedDbTableCipherKey(COLLECTCARD_DB_NAME, TableNameForCipher.card_loan.name)?.cipherKey
        )

        cardBillScheduledSecret = decryptSecret(
            cipherClientService.getEncryptedDbTableCipherKey(COLLECTCARD_DB_NAME, TableNameForCipher.card_bill_scheduled.name)?.cipherKey
        )

        cardPaymentScheduledSecret = decryptSecret(
            cipherClientService.getEncryptedDbTableCipherKey(COLLECTCARD_DB_NAME, TableNameForCipher.card_payment_scheduled.name)?.cipherKey
        )

        cardTransactionSecret = decryptSecret(
            cipherClientService.getEncryptedDbTableCipherKey(COLLECTCARD_DB_NAME, TableNameForCipher.card_transaction.name)?.cipherKey
        )
    }

    private fun decryptSecret(encrypted: String?): String? {
        val sdkBytes = SdkBytes.fromByteArray(Base64.getDecoder().decode(encrypted))
        val decryptRequest = DecryptRequest.builder()
            .ciphertextBlob(sdkBytes)
            .build()
        val decryptResponse = kmsClient?.decrypt(decryptRequest)
        return Base64Util.encode(decryptResponse?.plaintext()?.asByteArray())
    }

    override fun getSecret(keyAlias: KeyManagementService.KeyAlias?): String? {
        return when (keyAlias) {
            KeyManagementService.KeyAlias.api_log -> apiLogSecret
            KeyManagementService.KeyAlias.card -> cardSecret
            KeyManagementService.KeyAlias.card_bill -> cardBillSecret
            KeyManagementService.KeyAlias.card_bill_transaction -> cardBillTransactionSecret
            KeyManagementService.KeyAlias.card_loan -> cardLoanSecret
            KeyManagementService.KeyAlias.card_bill_scheduled -> cardBillScheduledSecret
            KeyManagementService.KeyAlias.card_payment_scheduled -> cardPaymentScheduledSecret
            KeyManagementService.KeyAlias.card_transaction -> cardTransactionSecret
            else -> throw CollectcardException("invalid key alias")
        }
    }

    override fun getIv(keyAlias: KeyManagementService.KeyAlias?): String? {
        return when (keyAlias) {
            KeyManagementService.KeyAlias.api_log -> apiLogIv
            KeyManagementService.KeyAlias.card -> cardIv
            KeyManagementService.KeyAlias.card_bill -> cardBillIv
            KeyManagementService.KeyAlias.card_bill_transaction -> cardBillTransactionIv
            KeyManagementService.KeyAlias.card_loan -> cardLoanIv
            KeyManagementService.KeyAlias.card_bill_scheduled -> cardBillScheduledIv
            KeyManagementService.KeyAlias.card_payment_scheduled -> cardPaymentScheduledIv
            KeyManagementService.KeyAlias.card_transaction -> cardTransactionIv
            else -> throw CollectcardException("invalid key alias")
        }
    }
}
