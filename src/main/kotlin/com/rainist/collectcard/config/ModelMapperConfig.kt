package com.rainist.collectcard.config

import java.math.BigDecimal
import org.modelmapper.AbstractConverter
import org.modelmapper.ModelMapper
import org.modelmapper.convention.MatchingStrategies
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ModelMapperConfig {

    @Bean
    fun modelMapper(): ModelMapper {
        val modelMapper = ModelMapper()
        modelMapper.configuration.matchingStrategy = MatchingStrategies.STRICT
        modelMapper.configuration.isSkipNullEnabled = true
        modelMapper.addConverter(bigDecimalConverter())
        return modelMapper
    }

    /* Entity -> Dto 방향 */
    fun bigDecimalConverter(): AbstractConverter<BigDecimal?, BigDecimal?> {
        return object : AbstractConverter<BigDecimal?, BigDecimal?>() {
            override fun convert(source: BigDecimal?): BigDecimal? {
                return source?.let { source.setScale(0) }
            }
        }
    }
}
