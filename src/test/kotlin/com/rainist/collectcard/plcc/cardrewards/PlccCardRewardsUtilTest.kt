package com.rainist.collectcard.plcc.cardrewards

import com.rainist.collectcard.plcc.common.util.PlccCardRewardsUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class PlccCardRewardsUtilTest {

    @DisplayName("minusAMonth 테스트")
    @Test
    fun minusAMonth_test() {
        // given
        val inquiryYearMonth1 = "202102"
        val inquiryYearMonth2 = "202111"

        // when
        val minusAMonth1 = PlccCardRewardsUtil.minusAMonth(inquiryYearMonth1)
        val minusAMonth2 = PlccCardRewardsUtil.minusAMonth(inquiryYearMonth2)

        // then
        assertThat(minusAMonth1).isEqualTo("202101")
        assertThat(minusAMonth2).isEqualTo("202110")
    }
}
