package com.example.notespote.data.local.dao

import androidx.room.Dao

import androidx.room.*
import com.example.notespote.data.local.entities.SeguimientoEntity
import com.example.notespote.data.local.entities.SyncStatus
import com.example.notespote.data.local.entities.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SeguimientoDao {

    @Query("SELECT * FROM seguimiento WHERE id_seguidor = :userId AND is_deleted = 0 ORDER BY fecha_seguimiento DESC")
    fun getSeguidos(userId: String): Flow<List<SeguimientoEntity>>

    @Query("SELECT * FROM seguimiento WHERE id_seguido = :userId AND is_deleted = 0 ORDER BY fecha_seguimiento DESC")
    fun getSeguidores(userId: String): Flow<List<SeguimientoEntity>>

    @Transaction
    @Query("""
        SELECT u.* FROM usuario u
        INNER JOIN seguimiento s ON u.id_usuario = s.id_seguido
        WHERE s.id_seguidor = :userId AND s.is_deleted = 0 AND u.is_deleted = 0
        ORDER BY s.fecha_seguimiento DESC
    """)
    fun getSeguidosDetalle(userId: String): Flow<List<UsuarioEntity>>

    @Transaction
    @Query("""
        SELECT u.* FROM usuario u
        INNER JOIN seguimiento s ON u.id_usuario = s.id_seguidor
        WHERE s.id_seguido = :userId AND s.is_deleted = 0 AND u.is_deleted = 0
        ORDER BY s.fecha_seguimiento DESC
    """)
    fun getSeguidoresDetalle(userId: String): Flow<List<UsuarioEntity>>

    @Query("SELECT * FROM seguimiento WHERE id_seguidor = :seguidorId AND id_seguido = :seguidoId AND is_deleted = 0")
    suspend fun getSeguimiento(seguidorId: String, seguidoId: String): SeguimientoEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM seguimiento WHERE id_seguidor = :seguidorId AND id_seguido = :seguidoId AND is_deleted = 0)")
    suspend fun isSiguiendo(seguidorId: String, seguidoId: String): Boolean

    @Query("SELECT COUNT(*) FROM seguimiento WHERE id_seguidor = :userId AND is_deleted = 0")
    suspend fun getSigueCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM seguimiento WHERE id_seguido = :userId AND is_deleted = 0")
    suspend fun getSeguidoresCount(userId: String): Int

    @Query("SELECT * FROM seguimiento WHERE sync_status != 'SYNCED' AND is_deleted = 0")
    suspend fun getPendingSyncSeguimientos(): List<SeguimientoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(seguimiento: SeguimientoEntity)

    @Update
    suspend fun update(seguimiento: SeguimientoEntity)

    @Query("UPDATE seguimiento SET is_deleted = 1, sync_status = 'PENDING_DELETE' WHERE id_seguimiento = :seguimientoId")
    suspend fun markAsDeleted(seguimientoId: String)

    @Query("UPDATE seguimiento SET is_deleted = 1, sync_status = 'PENDING_DELETE' WHERE id_seguidor = :seguidorId AND id_seguido = :seguidoId")
    suspend fun markAsDeletedByUsuarios(seguidorId: String, seguidoId: String)

    @Query("UPDATE seguimiento SET sync_status = :status WHERE id_seguimiento = :seguimientoId")
    suspend fun updateSyncStatus(seguimientoId: String, status: SyncStatus)

    @Query("DELETE FROM seguimiento WHERE id_seguimiento = :seguimientoId")
    suspend fun delete(seguimientoId: String)

    @Query("DELETE FROM seguimiento WHERE id_seguidor = :seguidorId AND id_seguido = :seguidoId")
    suspend fun deleteByUsuarios(seguidorId: String, seguidoId: String)
}