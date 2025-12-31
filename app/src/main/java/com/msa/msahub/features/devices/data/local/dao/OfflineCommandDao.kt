package com.msa.msahub.features.devices.data.local.dao

import androidx.room.*
import com.msa.msahub.features.devices.data.local.entity.OfflineCommandEntity
import com.msa.msahub.features.devices.data.local.entity.OfflineCommandStatus

@Dao
interface OfflineCommandDao {

    @Query("""
        SELECT * FROM offline_commands 
        WHERE status = :pending 
          AND attempts < maxAttempts 
        ORDER BY createdAtMillis ASC 
        LIMIT :limit
    """)
    suspend fun getReadyCommands(
        limit: Int, 
        pending: OfflineCommandStatus = OfflineCommandStatus.PENDING
    ): List<OfflineCommandEntity>

    @Query("""
        UPDATE offline_commands 
        SET status = :sending, 
            lastAttemptAtMillis = :now,
            updatedAtMillis = :now
        WHERE id = :id AND status = :pending
    """)
    suspend fun markSendingIfPending(
        id: String, 
        now: Long, 
        pending: OfflineCommandStatus = OfflineCommandStatus.PENDING,
        sending: OfflineCommandStatus = OfflineCommandStatus.SENDING
    ): Int

    @Query("""
        UPDATE offline_commands 
        SET status = :pending, 
            attempts = attempts + 1, 
            lastError = :error, 
            updatedAtMillis = :now 
        WHERE id = :id
    """)
    suspend fun markFailedAndRetry(id: String, error: String?, now: Long, pending: OfflineCommandStatus = OfflineCommandStatus.PENDING)

    @Query("""
        UPDATE offline_commands 
        SET status = :failed, 
            attempts = attempts + 1, 
            lastError = :error, 
            updatedAtMillis = :now 
        WHERE id = :id
    """)
    suspend fun markPermanentlyFailed(id: String, error: String?, now: Long, failed: OfflineCommandStatus = OfflineCommandStatus.FAILED_PERMANENT)

    @Query("""
        UPDATE offline_commands 
        SET status = :sent, 
            updatedAtMillis = :now 
        WHERE id = :id
    """)
    suspend fun markAsSent(id: String, now: Long, sent: OfflineCommandStatus = OfflineCommandStatus.SENT)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(command: OfflineCommandEntity)

    @Query("DELETE FROM offline_commands WHERE status = :sent AND updatedAtMillis < :threshold")
    suspend fun deleteOldSentCommands(threshold: Long, sent: OfflineCommandStatus = OfflineCommandStatus.SENT)

    @Query("DELETE FROM offline_commands")
    suspend fun clear()
}
