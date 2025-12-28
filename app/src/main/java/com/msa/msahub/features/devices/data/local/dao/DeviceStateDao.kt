package com.msa.msahub.features.devices.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msa.msahub.features.devices.data.local.entity.DeviceStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceStateDao {

    @Query("SELECT * FROM device_states WHERE deviceId = :deviceId ORDER BY updatedAtMillis DESC LIMIT 1")
    fun observeLatest(deviceId: String): Flow<DeviceStateEntity?>

    @Query("SELECT * FROM device_states WHERE deviceId = :deviceId ORDER BY updatedAtMillis DESC LIMIT :limit")
    suspend fun getRecent(deviceId: String, limit: Int): List<DeviceStateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(state: DeviceStateEntity)

    @Query("DELETE FROM device_states WHERE deviceId = :deviceId")
    suspend fun clearForDevice(deviceId: String)
}
