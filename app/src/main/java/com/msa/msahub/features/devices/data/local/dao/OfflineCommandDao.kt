package com.msa.msahub.features.devices.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msa.msahub.features.devices.data.local.entity.OfflineCommandEntity

@Dao
interface OfflineCommandDao {

    @Query("SELECT * FROM offline_commands ORDER BY createdAtMillis ASC")
    suspend fun getAll(): List<OfflineCommandEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: OfflineCommandEntity)

    @Query("DELETE FROM offline_commands WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM offline_commands")
    suspend fun clear()
}
