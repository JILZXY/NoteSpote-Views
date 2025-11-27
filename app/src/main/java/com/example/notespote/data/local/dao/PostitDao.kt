package com.example.notespote.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.notespote.data.local.entities.PostitEntity
import com.example.notespote.data.local.entities.SyncStatus
import kotlinx.coroutines.flow.Flow


@Dao
interface PostitDao {
    @Query("SELECT * FROM postit WHERE id_apunte = :apunteId AND is_deleted = 0 ORDER BY orden_z ASC")
    fun getPostitsByApunte(apunteId: String): Flow<List<PostitEntity>>

    @Query("SELECT * FROM postit WHERE id_postit = :postitId AND is_deleted = 0")
    suspend fun getPostitById(postitId: String): PostitEntity?

    @Query("SELECT * FROM postit WHERE sync_status != 'SYNCED' AND is_deleted = 0")
    suspend fun getPendingSyncPostits(): List<PostitEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(postit: PostitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(postits: List<PostitEntity>)

    @Update
    suspend fun update(postit: PostitEntity)

    @Query("UPDATE postit SET is_deleted = 1, sync_status = 'PENDING_DELETE' WHERE id_postit = :postitId")
    suspend fun markAsDeleted(postitId: String)

    @Query("UPDATE postit SET sync_status = :status WHERE id_postit = :postitId")
    suspend fun updateSyncStatus(postitId: String, status: SyncStatus)

    @Query("UPDATE postit SET posicion_x = :x, posicion_y = :y WHERE id_postit = :postitId")
    suspend fun updatePosicion(postitId: String, x: Int, y: Int)

    @Query("UPDATE postit SET ancho = :ancho, alto = :alto WHERE id_postit = :postitId")
    suspend fun updateTamano(postitId: String, ancho: Int, alto: Int)

    @Query("UPDATE postit SET orden_z = :ordenZ WHERE id_postit = :postitId")
    suspend fun updateOrdenZ(postitId: String, ordenZ: Int)

    @Query("DELETE FROM postit WHERE id_postit = :postitId")
    suspend fun delete(postitId: String)

    @Query("DELETE FROM postit WHERE id_apunte = :apunteId")
    suspend fun deleteByApunte(apunteId: String)
}