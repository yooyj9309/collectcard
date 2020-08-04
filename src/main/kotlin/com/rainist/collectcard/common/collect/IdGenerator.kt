package com.rainist.collectcard.common.collect

import com.rainist.collect.common.api.Api
import com.rainist.collect.common.log.IIdGenerator
import java.util.UUID
import org.springframework.stereotype.Component

@Component
class IdGenerator : IIdGenerator {
    override fun generate(api: Api): String = UUID.randomUUID().toString()
}
