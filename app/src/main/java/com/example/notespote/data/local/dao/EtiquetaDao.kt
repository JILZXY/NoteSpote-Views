package com.example.notespote.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.notespote.data.local.entities.EtiquetaEntity
import com.example.notespote.data.local.entities.SyncStatus
import kotlinx.coroutines.flow.Flow


@Dao
interface EtiquetaDao {
    @Query("SELECT * FROM etiqueta ORDER BY veces_usada DESC")
    fun getAllEtiquetas(): Flow<List<EtiquetaEntity>>

    @Query("SELECT * FROM etiqueta WHERE id_etiqueta = :id")
    suspend fun getEtiquetaById(id: String): EtiquetaEntity?

    @Query("SELECT * FROM etiqueta WHERE id_etiqueta = :id")
    fun getEtiquetaByIdFlow(id: String): Flow<EtiquetaEntity>

    @Query("SELECT * FROM etiqueta WHERE nombre_etiqueta = :nombre")
    suspend fun getEtiquetaByNombre(nombre: String): EtiquetaEntity?

    @Query("SELECT * FROM etiqueta WHERE nombre_etiqueta LIKE '%' || :query || '%' ORDER BY veces_usada DESC LIMIT :limit")
    fun searchEtiquetas(query: String, limit: Int = 10): Flow<List<EtiquetaEntity>>

    @Query("SELECT * FROM etiqueta ORDER BY veces_usada DESC LIMIT :limit")
    fun getTopEtiquetas(limit: Int = 10): Flow<List<EtiquetaEntity>>

    @Query("SELECT * FROM etiqueta WHERE sync_status != 'SYNCED'")
    suspend fun getPendingSyncEtiquetas(): List<EtiquetaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(etiqueta: EtiquetaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(etiquetas: List<EtiquetaEntity>)

    @Update
    suspend fun update(etiqueta: EtiquetaEntity)

    @Query("UPDATE etiqueta SET veces_usada = veces_usada + 1 WHERE id_etiqueta = :id")
    suspend fun incrementarUso(id: String)

    @Query("UPDATE etiqueta SET veces_usada = veces_usada - 1 WHERE id_etiqueta = :id AND veces_usada > 0")
    suspend fun decrementarUso(id: String)

    @Query("UPDATE etiqueta SET sync_status = :status WHERE id_etiqueta = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)

    @Query("DELETE FROM etiqueta WHERE id_etiqueta = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM etiqueta WHERE veces_usada = 0")
    suspend fun deleteUnused()
}