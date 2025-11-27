package com.example.notespote.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.notespote.data.local.entities.EtiquetaApunteEntity
import com.example.notespote.data.local.entities.EtiquetaEntity
import com.example.notespote.data.local.entities.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface EtiquetaApunteDao {
    @Query("SELECT * FROM etiqueta_apunte WHERE id_apunte = :apunteId AND is_deleted = 0")
    fun getEtiquetasByApunte(apunteId: String): Flow<List<EtiquetaApunteEntity>>

    @Transaction
    @Query("""
        SELECT e.* FROM etiqueta e
        INNER JOIN etiqueta_apunte ea ON e.id_etiqueta = ea.id_etiqueta
        WHERE ea.id_apunte = :apunteId AND ea.is_deleted = 0
    """)
    fun getEtiquetasDetalleByApunte(apunteId: String): Flow<List<EtiquetaEntity>>

    @Query("SELECT * FROM etiqueta_apunte WHERE id_etiqueta = :etiquetaId AND is_deleted = 0")
    fun getApuntesByEtiqueta(etiquetaId: String): Flow<List<EtiquetaApunteEntity>>

    @Query("SELECT * FROM etiqueta_apunte WHERE id_etiqueta_apunte = :id AND is_deleted = 0")
    suspend fun getEtiquetaApunteById(id: String): EtiquetaApunteEntity?

    @Query("SELECT * FROM etiqueta_apunte WHERE id_apunte = :apunteId AND id_etiqueta = :etiquetaId AND is_deleted = 0")
    suspend fun getEtiquetaApunte(apunteId: String, etiquetaId: String): EtiquetaApunteEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM etiqueta_apunte WHERE id_apunte = :apunteId AND id_etiqueta = :etiquetaId AND is_deleted = 0)")
    suspend fun exists(apunteId: String, etiquetaId: String): Boolean

    @Query("SELECT * FROM etiqueta_apunte WHERE sync_status != 'SYNCED' AND is_deleted = 0")
    suspend fun getPendingSyncEtiquetasApuntes(): List<EtiquetaApunteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(etiquetaApunte: EtiquetaApunteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(etiquetasApuntes: List<EtiquetaApunteEntity>)

    @Update
    suspend fun update(etiquetaApunte: EtiquetaApunteEntity)

    @Query("UPDATE etiqueta_apunte SET is_deleted = 1, sync_status = 'PENDING_DELETE' WHERE id_etiqueta_apunte = :id")
    suspend fun markAsDeleted(id: String)

    @Query("UPDATE etiqueta_apunte SET is_deleted = 1, sync_status = 'PENDING_DELETE' WHERE id_apunte = :apunteId AND id_etiqueta = :etiquetaId")
    suspend fun markAsDeletedByApunteAndEtiqueta(apunteId: String, etiquetaId: String)

    @Query("UPDATE etiqueta_apunte SET sync_status = :status WHERE id_etiqueta_apunte = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)

    @Query("DELETE FROM etiqueta_apunte WHERE id_etiqueta_apunte = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM etiqueta_apunte WHERE id_apunte = :apunteId")
    suspend fun deleteByApunte(apunteId: String)

    @Query("DELETE FROM etiqueta_apunte WHERE id_etiqueta = :etiquetaId")
    suspend fun deleteByEtiqueta(etiquetaId: String)
}