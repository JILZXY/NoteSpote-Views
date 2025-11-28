package com.example.notespote.data.local.dao

import androidx.room.*
import com.example.notespote.data.local.entities.LikeEntity
import com.example.notespote.data.local.entities.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface LikeDao {

    @Query("SELECT * FROM like_table WHERE id_usuario = :userId AND is_deleted = 0 ORDER BY fecha_like DESC")
    fun getLikesByUser(userId: String): Flow<List<LikeEntity>>

    @Query("SELECT * FROM like_table WHERE id_apunte = :apunteId AND is_deleted = 0")
    fun getLikesByApunte(apunteId: String): Flow<List<LikeEntity>>

    @Query("SELECT * FROM like_table WHERE id_usuario = :userId AND id_apunte = :apunteId AND is_deleted = 0")
    suspend fun getLike(userId: String, apunteId: String): LikeEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM like_table WHERE id_usuario = :userId AND id_apunte = :apunteId AND is_deleted = 0)")
    suspend fun hasLiked(userId: String, apunteId: String): Boolean

    @Query("SELECT COUNT(*) FROM like_table WHERE id_apunte = :apunteId AND is_deleted = 0")
    suspend fun getLikesCount(apunteId: String): Int

    @Query("SELECT * FROM like_table WHERE sync_status != 'SYNCED' AND is_deleted = 0")
    suspend fun getPendingSyncLikes(): List<LikeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(like: LikeEntity)

    @Update
    suspend fun update(like: LikeEntity)

    @Query("UPDATE like_table SET is_deleted = 1, sync_status = 'PENDING_DELETE' WHERE id_like = :likeId")
    suspend fun markAsDeleted(likeId: String)

    @Query("UPDATE like_table SET is_deleted = 1, sync_status = 'PENDING_DELETE' WHERE id_usuario = :userId AND id_apunte = :apunteId")
    suspend fun markAsDeletedByUserAndApunte(userId: String, apunteId: String)

    @Query("UPDATE like_table SET sync_status = :status WHERE id_like = :likeId")
    suspend fun updateSyncStatus(likeId: String, status: SyncStatus)

    @Query("DELETE FROM like_table WHERE id_like = :likeId")
    suspend fun delete(likeId: String)

    @Query("DELETE FROM like_table WHERE id_usuario = :userId AND id_apunte = :apunteId")
    suspend fun deleteByUserAndApunte(userId: String, apunteId: String)
}