package com.msa.msahub.features.scenes.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msa.msahub.features.scenes.data.local.entity.SceneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneDao {

    @Query("SELECT * FROM scenes ORDER BY updatedAt DESC")
    fun observeScenes(): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE id = :sceneId LIMIT 1")
    suspend fun getScene(sceneId: String): SceneEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SceneEntity)

    @Query("DELETE FROM scenes WHERE id = :sceneId")
    suspend fun delete(sceneId: String)
}
