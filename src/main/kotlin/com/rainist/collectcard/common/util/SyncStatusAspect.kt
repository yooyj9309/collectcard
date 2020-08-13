package com.rainist.collectcard.common.util

import com.rainist.collect.common.execution.ExecutionContext
import com.rainist.collectcard.common.service.UserSyncStatusService
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class SyncStatusAspect(private val userSyncStatusService: UserSyncStatusService) {

    @Around("@annotation(com.rainist.collectcard.common.util.SyncStatus)")
    private fun updateSyncStatus(joinPoint: ProceedingJoinPoint): Any {

        val methodSignature = joinPoint.signature as MethodSignature
        val syncStatus = methodSignature.method.getAnnotation(SyncStatus::class.java)

        val start = System.currentTimeMillis()
        val result = joinPoint.proceed()

        val executionContext = joinPoint.args.filter { arg ->
            arg is ExecutionContext
        }[0] as ExecutionContext?

        executionContext?.let {
            userSyncStatusService.updateUserSyncStatus(
                it.userId.toLong(),
                it.organizationId,
                syncStatus.transactionId,
                start
            )
        }
        return result
    }
}
