package com.rainist.collectcard.common.util

@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@kotlin.annotation.Target(AnnotationTarget.FUNCTION)
annotation class SyncStatus(val transactionId: String)
