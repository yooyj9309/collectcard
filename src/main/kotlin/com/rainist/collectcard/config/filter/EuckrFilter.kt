/*
package com.rainist.collectcard.config.filter

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.boot.web.servlet.filter.OrderedCharacterEncodingFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.CharacterEncodingFilter

@Configuration
class EuckrFilter {

    @Bean
    fun encodingFilterBean(): FilterRegistrationBean<CharacterEncodingFilter> {
        val registrationBean = FilterRegistrationBean<CharacterEncodingFilter>()
        val filter: CharacterEncodingFilter = OrderedCharacterEncodingFilter()
        filter.setForceEncoding(true)
        filter.encoding = "EUC-KR"
        registrationBean.filter = filter
        registrationBean.addUrlPatterns(
            "/v1/plcc/lottecard/card/issue"
        )
        return registrationBean
    }
}
*/
