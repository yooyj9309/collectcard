package com.rainist.collectcard.plcc.common.db.repository

import com.rainist.collectcard.plcc.common.db.entity.PlccCardRewardsHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PlccCardRewardsHistoryRepository : JpaRepository<PlccCardRewardsHistoryEntity, Long>
