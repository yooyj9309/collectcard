package com.rainist.collectcard.common.exception

class CollectcardException(message: String) : RuntimeException(message) {
    constructor(message: String, cause: Throwable) : this(message) {
        initCause(cause)
    }
}
