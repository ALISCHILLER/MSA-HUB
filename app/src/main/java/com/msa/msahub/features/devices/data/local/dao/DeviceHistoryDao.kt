package com.msa.msahub.features.devices.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msa.msahub.features.devices.data.local.entity.DeviceHistoryEntity

@Dao
interface DeviceHistoryDao {

    @Query("SELECT * FROM device_history WHERE deviceId = :deviceId ORDER BY recordedAtMillis DESC LIMIT :limit")
    suspend fun getForDevice(deviceId: String, limit: Int): List<DeviceHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: DeviceHistoryEntity)

    @Query("DELETE FROM device_history WHERE deviceId = :deviceId")
    suspend fun clearForDevice(deviceId: String)
}
