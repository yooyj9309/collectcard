package com.rainist.collectcard.config

import com.rainist.common.listener.ValidationListener
import com.rainist.common.log.Log
import com.rainist.common.service.ValidationService
import javax.validation.Validation
import javax.validation.Validator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
class ValidatorConfig {

    @Bean(name = ["validator"])
    fun validator(): Validator {
        return Validation.buildDefaultValidatorFactory().validator
    }

    @Bean("ValidationService")
    fun validationService(@Qualifier("validator") validator: Validator, validationEventListener: ValidationEventListener): ValidationService {
        return ValidationService(validator, validationEventListener)
    }
}

@Component
class ValidationEventListener : ValidationListener {
    companion object : Log

    override fun callBack(`object`: Any?, validationMsg: String) {
        logger.error("Validation Error EventListener Msg {} , {}", `object` ?: "", validationMsg)
        logger.withFieldError("Validation Error EventListener Object", `object` ?: "")
        logger.withFieldError("Validation Error EventListener Msg", validationMsg)
    }
}
