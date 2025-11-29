package com.example.notespote.data.local.dao

import androidx.room.*
import com.example.notespote.data.local.entities.CarpetaEntity
import com.example.notespote.data.local.entities.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface CarpetaDao {

    @Query("SELECT * FROM carpeta WHERE id_usuario = :userId AND is_deleted = 0 ORDER BY orden ASC")
    fun getCarpetasByUser(userId: String): Flow<List<CarpetaEntity>>

    @Query("SELECT * FROM carpeta WHERE id_carpeta = :carpetaId AND is_deleted = 0")
    suspend fun getCarpetaById(carpetaId: String): CarpetaEntity?

    @Query("SELECT * FROM carpeta WHERE id_carpeta_padre = :carpetaPadreId AND is_deleted = 0 ORDER BY orden ASC")
    fun getSubcarpetas(carpetaPadreId: String): Flow<List<CarpetaEntity>>

    @Query("SELECT * FROM carpeta WHERE id_carpeta_padre IS NULL AND id_usuario = :userId AND is_deleted = 0 ORDER BY orden ASC")
    fun getCarpetasRaiz(userId: String): Flow<List<CarpetaEntity>>

    @Query("SELECT * FROM carpeta WHERE id_materia = :materiaId AND is_deleted = 0 ORDER BY orden ASC")
    fun getCarpetasByMateria(materiaId: String): Flow<List<CarpetaEntity>>

    @Query("SELECT * FROM carpeta WHERE sync_status != 'SYNCED' AND is_deleted = 0")
    suspend fun getPendingSyncCarpetas(): List<CarpetaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(carpeta: CarpetaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(carpetas: List<CarpetaEntity>)

    @Update
    suspend fun update(carpeta: CarpetaEntity)

    @Query("UPDATE carpeta SET is_deleted = 1, sync_status = 'PENDING_DELETE' WHERE id_carpeta = :carpetaId")
    suspend fun markAsDeleted(carpetaId: String)

    @Query("UPDATE carpeta SET sync_status = :status WHERE id_carpeta = :carpetaId")
    suspend fun updateSyncStatus(carpetaId: String, status: SyncStatus)

    @Query("UPDATE carpeta SET orden = :orden WHERE id_carpeta = :carpetaId")
    suspend fun updateOrden(carpetaId: String, orden: Int)

    @Query("DELETE FROM carpeta WHERE id_carpeta = :carpetaId")
    suspend fun delete(carpetaId: String)

    @Transaction
    @Query("SELECT * FROM carpeta WHERE id_carpeta = :carpetaId")
    suspend fun getCarpetaWithSubcarpetas(carpetaId: String): CarpetaWithSubcarpetas?
}

data class CarpetaWithSubcarpetas(
    @Embedded val carpeta: CarpetaEntity,
    @Relation(
        parentColumn = "id_carpeta",
        entityColumn = "id_carpeta_padre"
    )
    val subcarpetas: List<CarpetaEntity>
)