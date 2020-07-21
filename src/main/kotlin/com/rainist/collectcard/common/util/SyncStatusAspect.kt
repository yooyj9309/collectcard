package com.rainist.collectcard.common.util

import com.rainist.collectcard.common.dto.SyncRequest
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

        val syncRequest = joinPoint.args.filter { arg ->
            arg is SyncRequest
        }[0] as SyncRequest?

        syncRequest?.let {
            userSyncStatusService.updateUserSyncStatus(
                it.banksaladUserId.toLong(),
                it.organizationId,
                syncStatus.transactionId,
                start
            )
        }
        return result
    }
}
