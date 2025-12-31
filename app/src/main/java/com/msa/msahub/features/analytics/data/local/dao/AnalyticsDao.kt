package com.msa.msahub.features.analytics.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msa.msahub.features.analytics.data.local.entity.SensorAnalyticsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalyticsDao {
    @Query("SELECT * FROM sensor_analytics WHERE deviceId = :deviceId AND metricType = :type ORDER BY dateMillis DESC LIMIT :limit")
    fun observeTrends(deviceId: String, type: String, limit: Int = 30): Flow<List<SensorAnalyticsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SensorAnalyticsEntity)

    @Query("DELETE FROM sensor_analytics WHERE dateMillis < :cutoff")
    suspend fun deleteOldAnalytics(cutoff: Long)
}
