package com.rainist.collectcard.organization

import com.rainist.collectcard.common.organization.Organizations
import org.junit.Assert
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@DisplayName("Organization 정보")
class OrganizationTest {

    @Value("\${shinhancard.clientId}")
    lateinit var shinhancardClientId: String

    @Value("\${shinhancard.organizationObjectId}")
    lateinit var shinhancardOrganizationObjectId: String

    @Test
    fun shinhancardOrganizationTest() {
        val cardOrganization = Organizations.valueOf(shinhancardOrganizationObjectId)

        Assert.assertEquals(shinhancardClientId, cardOrganization.clientId)
        Assert.assertEquals(shinhancardOrganizationObjectId, cardOrganization.organizationObjectId)
    }
}
