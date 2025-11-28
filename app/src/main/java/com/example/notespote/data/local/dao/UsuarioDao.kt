package com.example.notespote.data.local.dao

import androidx.room.*
import com.example.notespote.data.local.entities.SyncStatus
import com.example.notespote.data.local.entities.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuario WHERE id_usuario = :userId AND is_deleted = 0")
    suspend fun getUsuarioById(userId: String): UsuarioEntity?

    @Query("SELECT * FROM usuario WHERE email = :email AND is_deleted = 0")
    suspend fun getUsuarioByEmail(email: String): UsuarioEntity?

    @Query("SELECT * FROM usuario WHERE nombre_usuario = :username AND is_deleted = 0")
    suspend fun getUsuarioByUsername(username: String): UsuarioEntity?

    @Query("SELECT * FROM usuario WHERE id_usuario = :userId AND is_deleted = 0")
    fun getUsuarioByIdFlow(userId: String): Flow<UsuarioEntity?>

    @Query("SELECT * FROM usuario WHERE sync_status != 'SYNCED' AND is_deleted = 0")
    suspend fun getPendingSyncUsuarios(): List<UsuarioEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usuario: UsuarioEntity)

    @Update
    suspend fun update(usuario: UsuarioEntity)

    @Query("UPDATE usuario SET is_deleted = 1, sync_status = 'PENDING_DELETE' WHERE id_usuario = :userId")
    suspend fun markAsDeleted(userId: String)

    @Query("UPDATE usuario SET sync_status = :status WHERE id_usuario = :userId")
    suspend fun updateSyncStatus(userId: String, status: SyncStatus)

    @Query("UPDATE usuario SET ultima_conexion = :timestamp WHERE id_usuario = :userId")
    suspend fun updateUltimaConexion(userId: String, timestamp: Long)

    @Query("UPDATE usuario SET total_likes_recibidos = :totalLikes WHERE id_usuario = :userId")
    suspend fun updateTotalLikes(userId: String, totalLikes: Int)

    @Query("UPDATE usuario SET total_seguidores = :totalSeguidores WHERE id_usuario = :userId")
    suspend fun updateTotalSeguidores(userId: String, totalSeguidores: Int)

    @Query("UPDATE usuario SET total_seguidos = :totalSeguidos WHERE id_usuario = :userId")
    suspend fun updateTotalSeguidos(userId: String, totalSeguidos: Int)

    @Query("DELETE FROM usuario WHERE id_usuario = :userId")
    suspend fun delete(userId: String)
}