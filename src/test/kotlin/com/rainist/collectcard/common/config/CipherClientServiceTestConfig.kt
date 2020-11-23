package com.rainist.collectcard.common.config

import com.github.banksalad.idl.apis.v1.cipher.CipherProto.GetEncryptedDbTableCipherKeyResponse
import com.rainist.collectcard.common.service.KeyManagementServiceImpl
import com.rainist.collectcard.grpc.client.CipherClientService
import org.mockito.Mockito
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

@Configuration
@Profile("test")
class CipherClientServiceTestConfig {

    val cardCipherKey = "AQIDAHj7+3rJ/PnKcdxwp2TjDtYsrEO+KLSrAtcFx+Cr5AN8gAFfnyixN5mHsZsVMVF7jJT7AAAAfjB8BgkqhkiG9w0BBwagbzBtAgEAMGgGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQM6dliYgNxW8ERlGujAgEQgDv4e4VlNTAO3B47rY9q3RRGw2E3q7DMHGa0r+9zDa272BzVCBbVS2sI5Foz9imqORaDr1SE4019s4y60Q=="
    val cardBillCipherKey = "AQIDAHj7+3rJ/PnKcdxwp2TjDtYsrEO+KLSrAtcFx+Cr5AN8gAEM7C4H7jYSZZaDvfiglcKeAAAAfjB8BgkqhkiG9w0BBwagbzBtAgEAMGgGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQM9QzO+mRAx+8iT5zbAgEQgDuzoBGRavKAtgREPk2Nuzn5nMLN7qLRLo1hBcjt8du+G+ZsdnTk9i970Q52uD6HKUCqDKmow5Q3eDbqBg=="
    val cardBillTransactionCipherKey = "AQIDAHj7+3rJ/PnKcdxwp2TjDtYsrEO+KLSrAtcFx+Cr5AN8gAFWWIC6ipaH1nByiXkqhjfwAAAAfjB8BgkqhkiG9w0BBwagbzBtAgEAMGgGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMpTa7VsogukYDzSMiAgEQgDtCZXT6BWzkiwFHQaFmB5xpDPAhepRiKPaBsWx9/njHIOV6JCGwFRJe3iLZI0XNUjTa8ofKIcbbvHoVBA=="
    val cardLoanCipherKey = "AQIDAHj7+3rJ/PnKcdxwp2TjDtYsrEO+KLSrAtcFx+Cr5AN8gAGOrxDln2W5gMcupUN77W3qAAAAfjB8BgkqhkiG9w0BBwagbzBtAgEAMGgGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMEPajrf41qALSkLdrAgEQgDsj6a0wD8OVec1qFev2Be1Mq3EHMsSESb33W13yWYsqC+IkKCBKy7sNOc8DsIWosoLjRMDwbPuayx8CnQ=="
    val cardPaymentScheduledCipherKey = "AQIDAHj7+3rJ/PnKcdxwp2TjDtYsrEO+KLSrAtcFx+Cr5AN8gAFKy1Ukv6qWiA9fCROWXO2+AAAAfjB8BgkqhkiG9w0BBwagbzBtAgEAMGgGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMYYLo903l5U+BJTMFAgEQgDtEolmiglnZ8KKqu7N6r5Lt6CWmz0QSDhsEocEEtc5qNwDad20NnGaiPm1fOK5N8+lc0YqAFmfQzwwltw=="
    val cardTransactionCipherKey = "AQIDAHj7+3rJ/PnKcdxwp2TjDtYsrEO+KLSrAtcFx+Cr5AN8gAFjJYSQdnMFECexxJn39AylAAAAfjB8BgkqhkiG9w0BBwagbzBtAgEAMGgGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMR9IDKzCUDK/fk6mFAgEQgDvyvSOFa/UWOxaq5egiSazBOCHpwg+RN1SgrhPRZhI4r3UY4033HEjDaXIBBxM5qKLoAbHfV7nOYw6jkw=="
    val apiLogCipherKey = "AQIDAHj7+3rJ/PnKcdxwp2TjDtYsrEO+KLSrAtcFx+Cr5AN8gAFVZZ3/xqEiWq7FuP7NLqb7AAAAfjB8BgkqhkiG9w0BBwagbzBtAgEAMGgGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMVq5HgJgC2fFXJjcoAgEQgDvMgRR6j5Ri1i1BBp+YqXTS1KnIErW+EqmWmkleN+NNRw291ltFR68ZcZFXgzIa+zL65XWMBH7RmTxcWw=="

    @Bean
    @Primary
    fun cipherClientService(): CipherClientService? {
        val cipherClientService: CipherClientService = Mockito.mock(CipherClientService::class.java)

        Mockito
            .`when`(cipherClientService.getEncryptedDbTableCipherKey(
                KeyManagementServiceImpl.COLLECTCARD_DB_NAME,
                KeyManagementServiceImpl.TableNameForCipher.card.name)
            )
            .thenReturn(
                GetEncryptedDbTableCipherKeyResponse.newBuilder().setCipherKey(cardCipherKey).build()
            )

        Mockito
            .`when`(cipherClientService.getEncryptedDbTableCipherKey(
                KeyManagementServiceImpl.COLLECTCARD_DB_NAME,
                KeyManagementServiceImpl.TableNameForCipher.card_bill.name)
            )
            .thenReturn(
                GetEncryptedDbTableCipherKeyResponse.newBuilder().setCipherKey(cardBillCipherKey).build()
            )

        Mockito
            .`when`(cipherClientService.getEncryptedDbTableCipherKey(
                KeyManagementServiceImpl.COLLECTCARD_DB_NAME,
                KeyManagementServiceImpl.TableNameForCipher.card_bill_transaction.name)
            )
            .thenReturn(
                GetEncryptedDbTableCipherKeyResponse.newBuilder().setCipherKey(cardBillTransactionCipherKey).build()
            )

        Mockito
            .`when`(cipherClientService.getEncryptedDbTableCipherKey(
                KeyManagementServiceImpl.COLLECTCARD_DB_NAME,
                KeyManagementServiceImpl.TableNameForCipher.card_loan.name)
            )
            .thenReturn(
                GetEncryptedDbTableCipherKeyResponse.newBuilder().setCipherKey(cardLoanCipherKey).build()
            )

        Mockito
            .`when`(cipherClientService.getEncryptedDbTableCipherKey(
                KeyManagementServiceImpl.COLLECTCARD_DB_NAME,
                KeyManagementServiceImpl.TableNameForCipher.card_payment_scheduled.name)
            )
            .thenReturn(
                GetEncryptedDbTableCipherKeyResponse.newBuilder().setCipherKey(cardPaymentScheduledCipherKey).build()
            )

        Mockito
            .`when`(cipherClientService.getEncryptedDbTableCipherKey(
                KeyManagementServiceImpl.COLLECTCARD_DB_NAME,
                KeyManagementServiceImpl.TableNameForCipher.card_transaction.name)
            )
            .thenReturn(
                GetEncryptedDbTableCipherKeyResponse.newBuilder().setCipherKey(cardTransactionCipherKey).build()
            )

        Mockito
            .`when`(cipherClientService.getEncryptedDbTableCipherKey(
                KeyManagementServiceImpl.COLLECTCARD_DB_NAME,
                KeyManagementServiceImpl.TableNameForCipher.api_log.name)
            )
            .thenReturn(
                GetEncryptedDbTableCipherKeyResponse.newBuilder().setCipherKey(apiLogCipherKey).build()
            )

        return cipherClientService
    }
}
