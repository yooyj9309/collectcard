package com.rainist.collectcard.card

import java.lang.RuntimeException

class CardsException(message: String) : RuntimeException(message) {
    constructor(message: String, cause: Throwable) : this(message) {
        initCause(cause)
    }
}
