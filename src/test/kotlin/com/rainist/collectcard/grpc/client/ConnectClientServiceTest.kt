package com.rainist.collectcard.grpc.client

import com.github.banksalad.idl.apis.external.v1.connect.ConnectGrpc
import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto
import io.grpc.ManagedChannel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(value = [SpringExtension::class])
@ContextConfiguration(classes = [ConnectClient::class])
@Import(ConnectClientServiceTest.MockConfiguration::class)
class ConnectClientServiceTest {

    @TestConfiguration
    class MockConfiguration {
        @Bean("connectChannel")
        fun connectChannel(): ManagedChannel {
            return mock(ManagedChannel::class.java)
        }

        @Bean("connectStub")
        fun connectStub(connectChannel: ManagedChannel): ConnectGrpc.ConnectBlockingStub {
            return mock(ConnectGrpc.ConnectBlockingStub::class.java)
        }
    }

    @Autowired
    lateinit var connectClient: ConnectClient

    @MockBean
    @Qualifier("connectStub")
    lateinit var connectStub: ConnectGrpc.ConnectBlockingStub

    @Test
    @DisplayName("DI TEST")
    fun diTest() {
        Assertions.assertNotNull(connectClient)
    }

    @Test()
    @DisplayName("토큰 갱신 테스트")
    fun refreshTokenTest() {
        // given
        val banksaladUserId = "1"
        val organizationObjectid = "card"
        val req = ConnectProto.RefreshTokenRequest
            .newBuilder()
            .setBanksaladUserId(banksaladUserId)
            .setOrganizationObjectid(organizationObjectid)
            .build()

        val res = ConnectProto.RefreshTokenResponse
            .newBuilder()
            .setAccessToken("AccessToken")
            .build()

        given(connectStub.refreshToken(req)).willReturn(res)

        // when
        val response = connectClient.refreshToken(banksaladUserId, organizationObjectid)

        Assertions.assertNotNull(response)
        Assertions.assertEquals(response?.accessToken, res.accessToken)
    }

    @Test()
    @DisplayName("토큰 갱신 예외 테스트")
    fun refreshTokenFailTest() {
        // given
        val banksaladUserId = "1"
        val organizationObjectid = "card"
        val req = ConnectProto.RefreshTokenRequest
            .newBuilder()
            .setBanksaladUserId(banksaladUserId)
            .setOrganizationObjectid(organizationObjectid)
            .build()

        given(connectStub.refreshToken(req)).willThrow(RuntimeException::class.java)

        // when
        val exception = Assertions.assertThrows(RuntimeException::class.java) {
            connectClient.refreshToken(banksaladUserId, organizationObjectid)
        }

        Assertions.assertEquals(exception::class.java, RuntimeException::class.java)
    }

    @Test
    @DisplayName("토큰 발행 테스트")
    fun issueTokenTest() {
        // given
        val banksaladUserId = "1"
        val organizationObjectid = "card"
        val authorizationCode = "authorizationCode"

        val req = ConnectProto.IssueTokenRequest
            .newBuilder()
            .setAuthorizationCode(authorizationCode)
            .setBanksaladUserId(banksaladUserId)
            .setOrganizationObjectid(organizationObjectid)
            .build()

        val res = ConnectProto.IssueTokenResponse
            .newBuilder()
            .setAccessToken("accessToken")
            .setRefreshToken("refreshToken")
            .build()

        given(connectStub.issueToken(req)).willReturn(res)

        // when
        val response = connectClient.issueToken(authorizationCode, banksaladUserId, organizationObjectid)

        Assertions.assertNotNull(response)
        Assertions.assertEquals(response?.accessToken, res.accessToken)
        Assertions.assertEquals(response?.refreshToken, res.refreshToken)
    }

    @Test
    @DisplayName("토큰 발행 예외 테스트")
    fun issueTokenFailTest() {
        // given
        val banksaladUserId = "1"
        val organizationObjectid = "card"
        val authorizationCode = "authorizationCode"

        val req = ConnectProto.IssueTokenRequest
            .newBuilder()
            .setAuthorizationCode(authorizationCode)
            .setBanksaladUserId(banksaladUserId)
            .setOrganizationObjectid(organizationObjectid)
            .build()

        val res = ConnectProto.IssueTokenResponse
            .newBuilder()
            .setAccessToken("accessToken")
            .setRefreshToken("refreshToken")
            .build()

        given(connectStub.issueToken(req)).willThrow(RuntimeException::class.java)

        // when
        val exception = Assertions.assertThrows(RuntimeException::class.java) {
            connectClient.issueToken(authorizationCode, banksaladUserId, organizationObjectid)
        }

        Assertions.assertEquals(exception::class.java, RuntimeException::class.java)
    }

    @Test
    @DisplayName("AccessToken Get 테스트")
    fun getAccessToken() {
        // given
        val banksaladUserId = "1"
        val organizationObjectid = "card"

        val req = ConnectProto.GetAccessTokenRequest
            .newBuilder()
            .setBanksaladUserId(banksaladUserId)
            .setOrganizationObjectid(organizationObjectid)
            .build()

        val res = ConnectProto.GetAccessTokenResponse
            .newBuilder()
            .setAccessToken("accessToken")
            .build()

        given(connectStub.getAccessToken(req)).willReturn(res)

        // when
        val response = connectClient.getAccessToken(banksaladUserId, organizationObjectid)

        Assertions.assertNotNull(response)
        Assertions.assertEquals(response?.accessToken, res.accessToken)
    }

    @Test
    @DisplayName("AccessToken Get 예외 테스트")
    fun getAccessFailToken() {
        // given
        val banksaladUserId = "1"
        val organizationObjectid = "card"

        val req = ConnectProto.GetAccessTokenRequest
            .newBuilder()
            .setBanksaladUserId(banksaladUserId)
            .setOrganizationObjectid(organizationObjectid)
            .build()

        val res = ConnectProto.GetAccessTokenResponse
            .newBuilder()
            .setAccessToken("accessToken")
            .build()

        given(connectStub.getAccessToken(req)).willThrow(RuntimeException::class.java)

        // when
        val exception = Assertions.assertThrows(RuntimeException::class.java) {
            connectClient.getAccessToken(banksaladUserId, organizationObjectid)
        }

        Assertions.assertEquals(exception::class.java, RuntimeException::class.java)
    }
}
