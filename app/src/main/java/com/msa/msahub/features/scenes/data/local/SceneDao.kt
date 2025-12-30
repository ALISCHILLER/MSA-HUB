package com.msa.msahub.features.scenes.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneDao {

    @Query("SELECT * FROM scenes ORDER BY updatedAt DESC")
    fun observeScenes(): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): SceneEntity?

    @Upsert
    suspend fun upsert(entity: SceneEntity)

    @Delete
    suspend fun delete(entity: SceneEntity)
}
