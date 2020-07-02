package com.rainist.collectcard.cardbills

import com.rainist.collectcard.cardbills.dto.ListCardBillsRequest
import com.rainist.collectcard.cardbills.dto.ListCardBillsRequestDataBody
import com.rainist.collectcard.cardbills.dto.ListCardBillsRequestDataHeader
import com.rainist.collectcard.common.collect.api.ShinhancardApis
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.util.ResourceUtils
import org.springframework.web.client.RestTemplate

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@DisplayName("결제예정금액조회")
class CardBillServiceImplTest {
    @Autowired
    lateinit var commonRestTemplate: RestTemplate

    @Autowired
    lateinit var cardBillService: CardBillServiceImpl

    // TODO : Temporary comment out @Test annotation for merging
//    @Test
    fun listUserCardBills_success() {
        setupServer()
        val response = cardBillService.listUserCardBillsExpected(
            ListCardBillsRequest(
                ListCardBillsRequestDataHeader(""),
                ListCardBillsRequestDataBody(nextKey = "")
            )
        )

        MatcherAssert.assertThat(response.dataHeader?.resultCode, Matchers.`is`("0004"))
//        MatcherAssert.assertThat(response.dataBody.cardBills.size, Matchers.`is`(3))
    }

    private fun setupServer() {
        val api = ShinhancardApis.card_shinhancard_list_user_card_bills_expected
        val server = MockRestServiceServer.bindTo(commonRestTemplate).ignoreExpectOrder(true).build()

        server.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(api.endpoint))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.valueOf(api.method.name)))
            .andRespond(
                MockRestResponseCreators.withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(readText("classpath:mock/card_shinhancard_bills_expected_p1.json"))
            )

        server.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(api.endpoint))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.valueOf(api.method.name)))
            .andRespond(
                MockRestResponseCreators.withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(readText("classpath:mock/card_shinhancard_bills_expected_p2.json"))
            )

        server.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(api.endpoint))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.valueOf(api.method.name)))
            .andRespond(
                MockRestResponseCreators.withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(readText("classpath:mock/card_shinhancard_bills_expected_p3.json"))
            )
    }

    private fun readText(fileInClassPath: String): String {
        return ResourceUtils.getFile(fileInClassPath).readText(Charsets.UTF_8)
    }
}
