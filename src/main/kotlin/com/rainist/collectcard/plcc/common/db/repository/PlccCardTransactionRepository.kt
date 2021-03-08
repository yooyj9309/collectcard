package com.rainist.collectcard.plcc.common.db.repository

import com.rainist.collectcard.plcc.common.db.entity.PlccCardTransactionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PlccCardTransactionRepository : JpaRepository<PlccCardTransactionEntity, Long>
