package com.rainist.collectcard.common.service

import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto
import com.rainist.collectcard.grpc.client.ConnectClient
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@DisplayName("HeaderService")
class HeaderServiceTest {

    @Autowired
    lateinit var headerService: HeaderService

    @MockBean
    lateinit var connectClient: ConnectClient

    @Value("\${shinhancard.clientId}")
    lateinit var clientId: String

    @Value("\${shinhancard.organizationId}")
    lateinit var organizationId: String

    @Test
    fun headerServiceTest_shinhancard() {
        val banksaladUserId = "1"
        val organizationObjectId = "596d66692c4069c168b57c59"

        // grpc 호출부분 mocking
        BDDMockito.given(connectClient.getAccessToken(banksaladUserId, organizationObjectId))
            .willReturn(
                ConnectProto.GetAccessTokenResponse.newBuilder()
                    .setAccessToken("mockToken").build()
            )

        val header = headerService.makeHeader(banksaladUserId, organizationId)

        assertEquals("application/json", header["contentType"])
        assertEquals("Bearer mockToken", header["authorization"])
        assertEquals(clientId, header["clientId"])
    }
}
