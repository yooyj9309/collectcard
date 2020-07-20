package com.rainist.collectcard.common.service

import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@DisplayName("Organization")
internal class CardOrganizationServiceImplTest {

    @Autowired
    lateinit var organizationService: OrganizationService

    @Test
    fun getOrganizationByOrganizationId() {
        val organization = organizationService.getOrganizationByObjectId("596d66692c4069c168b57c59")

        MatcherAssert.assertThat(organization.organizationId, Is.`is`("shinhancard"))
        MatcherAssert.assertThat(organization.organizationObjectId, Is.`is`("596d66692c4069c168b57c59"))
    }

    @Test
    fun getOrganizationByObjectId() {
        val organization = organizationService.getOrganizationByOrganizationId("shinhancard")

        MatcherAssert.assertThat(organization.organizationId, Is.`is`("shinhancard"))
        MatcherAssert.assertThat(organization.organizationObjectId, Is.`is`("596d66692c4069c168b57c59"))
    }
}
