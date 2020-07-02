package com.rainist.collectcard.common.organization

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

    @Value("\${shinhancard.organizationObjectId}")
    lateinit var shinhancardOrganizationObjectId: String

    @PostConstruct
    fun init() {
        kotlin.runCatching {
            shinhancardOrganization = CardOrganization().apply {
                this.name = "신한카드"
                this.code = "SHC"
                this.clientId = shinhancardClientId
                this.organizationObjectId = shinhancardOrganizationObjectId
            }.also {
                validationService.validateOrThrows(it)
            }.also {
                map[shinhancardOrganizationObjectId] = it
            }
        }
        .onFailure {
            logger.withFieldError("OrganizationsInitError", it.localizedMessage, it)
            throw Exception("OrganizationsInitError")
        }
        .getOrThrow()
    }

    companion object : Log {
        private val map = LinkedHashMap<String, CardOrganization>()

        fun valueOf(organizationObjectId: String): CardOrganization {
            return map[organizationObjectId] ?: throw Exception("Organizations not found")
        }

        // 신한카드
        lateinit var shinhancardOrganization: CardOrganization
    }
}
