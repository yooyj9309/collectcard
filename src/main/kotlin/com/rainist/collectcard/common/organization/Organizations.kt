package com.rainist.collectcard.common.organization

import com.rainist.collect.common.exception.CollectException
import com.rainist.collectcard.common.exception.CollectcardException
import com.rainist.common.log.Log
import com.rainist.common.service.ValidationService
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class Organizations(
    val validationService: ValidationService
) {

    @Value("\${shinhancard.clientId}")
    lateinit var shinhancardClientId: String

    @Value("\${shinhancard.organizationObjectid}")
    lateinit var shinhancardOrganizationObjectid: String

    @PostConstruct
    fun init() {
        kotlin.runCatching {
            shinhancardOrganization = CardOrganization().apply {
                this.name = "shinhancard"
                this.code = "SHC"
                this.clientId = shinhancardClientId
                this.organizationObjectId = shinhancardOrganizationObjectid
            }.also {
                validationService.validateOrThrows(it)
            }.also {
                organizationsByObjectId[shinhancardOrganizationObjectid] = it
                organizationsByOrganizationId["shinhancard"] = it
            }
        }.onFailure {
            logger.withFieldError("OrganizationsInitError", it.localizedMessage, it)
            throw CollectException("OrganizationsInitError")
        }.getOrThrow()
    }

    companion object : Log {
        private val organizationsByObjectId = LinkedHashMap<String, CardOrganization>()
        private val organizationsByOrganizationId = LinkedHashMap<String, CardOrganization>()

        @Deprecated("deprecated")
        fun valueOf(organizationObjectid: String?): CardOrganization? {
            return organizationsByObjectId[organizationObjectid]
                ?: kotlin.run {
                    logger.withFieldError("OrganizationsNotFound", organizationObjectid ?: "null")
                    null
                }
        }

        fun valueOfCompanyId(companyId: String): CardOrganization {
            return organizationsByObjectId[companyId]
                ?: throw CollectcardException("Fail to resolve organization. companyId: $companyId")
        }

        fun valueOfOrganizationId(organizationId: String): CardOrganization {
            return organizationsByOrganizationId[organizationId]
                ?: throw CollectcardException("Fail to resolve organization. organizationId: $organizationId")
        }

        // 신한카드
        lateinit var shinhancardOrganization: CardOrganization
    }
}
