package com.rainist.collectcard.common.exception

class CollectcardException(message: String) : RuntimeException(message) {

    private var code: String? = null

    constructor(code: String, message: String) : this(message) {
        this.code = code
    }

    constructor(message: String, cause: Throwable) : this(message) {
        initCause(cause)
    }

    fun getCode(): String? {
        return code
    }
}
