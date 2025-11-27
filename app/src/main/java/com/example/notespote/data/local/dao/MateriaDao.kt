package com.example.notespote.data.local.dao

import androidx.room.*
import com.example.notespote.data.local.entities.MateriaEntity
import com.example.notespote.data.local.entities.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface MateriaDao {

    @Query("SELECT * FROM materia WHERE is_deleted = 0 ORDER BY nombre_materia ASC")
    fun getAllMaterias(): Flow<List<MateriaEntity>>

    @Query("SELECT * FROM materia WHERE id_materia = :materiaId AND is_deleted = 0")
    suspend fun getMateriaById(materiaId: String): MateriaEntity?

    @Query("SELECT * FROM materia WHERE nombre_materia = :nombre AND is_deleted = 0")
    suspend fun getMateriaByNombre(nombre: String): MateriaEntity?

    @Query("SELECT * FROM materia WHERE categoria = :categoria AND is_deleted = 0")
    fun getMateriasByCategoria(categoria: String): Flow<List<MateriaEntity>>

    @Query("SELECT * FROM materia WHERE sync_status != 'SYNCED' AND is_deleted = 0")
    suspend fun getPendingSyncMaterias(): List<MateriaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(materia: MateriaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(materias: List<MateriaEntity>)

    @Update
    suspend fun update(materia: MateriaEntity)

    @Query("UPDATE materia SET is_deleted = 1 WHERE id_materia = :materiaId")
    suspend fun markAsDeleted(materiaId: String)

    @Query("UPDATE materia SET sync_status = :status WHERE id_materia = :materiaId")
    suspend fun updateSyncStatus(materiaId: String, status: SyncStatus)

    @Query("DELETE FROM materia WHERE id_materia = :materiaId")
    suspend fun delete(materiaId: String)
}