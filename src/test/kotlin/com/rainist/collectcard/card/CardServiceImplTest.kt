package com.rainist.collectcard.card

import com.rainist.collectcard.common.collect.api.ShinhancardApis
import com.rainist.collectcard.common.service.CardOrganization
import com.rainist.collectcard.common.service.HeaderService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.util.ResourceUtils
import org.springframework.web.client.RestTemplate

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@DisplayName("보유카드조회")
class CardServiceImplTest {

    @Autowired
    lateinit var commonRestTemplate: RestTemplate

    @Autowired
    lateinit var cardService: CardServiceImpl

    @MockBean
    lateinit var headerService: HeaderService

    // TODO : add mocking for Repository
//    @Test
    fun listCard_success() {
        setupServer()

        val banksaladUserId = "1"
        val organization = CardOrganization().apply {
            organizationId = "organizationId"
        }

        given(headerService.makeHeader(banksaladUserId, organization))
            .willReturn(
                mutableMapOf(
                    "contentType" to MediaType.APPLICATION_JSON_VALUE,
                    "authorization" to "Bearer 123",
                    "clientId" to "596d66692c4069c168b57c59"
                )
            )

        val response = cardService.listCards(banksaladUserId, organization)

        assertThat(response.dataHeader?.resultCode, `is`("0004"))
        assertThat(response.dataBody?.cards?.size, `is`(4))
    }

    private fun setupServer() {
        val api = ShinhancardApis.card_shinhancard_cards
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        server.expect(ExpectedCount.manyTimes(), requestTo(api.endpoint))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.valueOf(api.method.name)))
            .andRespond(
                MockRestResponseCreators.withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(readText("classpath:mock/card_shinhancard_cards.json"))
            )
    }

    private fun readText(fileInClassPath: String): String {
        return ResourceUtils.getFile(fileInClassPath).readText(Charsets.UTF_8)
    }
}
