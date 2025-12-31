package com.msa.msahub.features.devices.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.msa.msahub.features.devices.data.local.entity.OfflineCommandEntity

@Dao
interface OfflineCommandDao {

    @Query("SELECT * FROM offline_commands ORDER BY createdAtMillis ASC")
    suspend fun getAll(): List<OfflineCommandEntity>

    @Query("SELECT * FROM offline_commands WHERE attempts < :maxAttempts ORDER BY createdAtMillis ASC LIMIT :limit")
    suspend fun getPending(limit: Int, maxAttempts: Int = 5): List<OfflineCommandEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: OfflineCommandEntity)

    @Update
    suspend fun update(item: OfflineCommandEntity)

    @Query("DELETE FROM offline_commands WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM offline_commands")
    suspend fun clear()
}
