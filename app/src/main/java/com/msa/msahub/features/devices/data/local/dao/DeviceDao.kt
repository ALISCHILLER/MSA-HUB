package com.msa.msahub.features.devices.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msa.msahub.features.devices.data.local.entity.DeviceEntity

@Dao
interface DeviceDao {

    @Query("SELECT * FROM devices ORDER BY name ASC")
    suspend fun getAll(): List<DeviceEntity>

    @Query("SELECT * FROM devices WHERE id = :deviceId LIMIT 1")
    suspend fun getById(deviceId: String): DeviceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<DeviceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: DeviceEntity)

    @Query("DELETE FROM devices")
    suspend fun clear()
}
