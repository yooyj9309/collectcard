package com.rainist.collectcard

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class CollectcardApplication

fun main(args: Array<String>) {
    runApplication<CollectcardApplication>(*args)
}
