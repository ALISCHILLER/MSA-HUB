package com.msa.msahub.features.automation.data.local.dao

import androidx.room.*
import com.msa.msahub.features.automation.data.local.entity.AutomationLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AutomationLogDao {
    @Query("SELECT * FROM automation_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentLogs(): Flow<List<AutomationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: AutomationLogEntity)

    @Query("DELETE FROM automation_logs WHERE timestamp < :threshold")
    suspend fun clearOldLogs(threshold: Long)
}
