package com.example.notespote.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.notespote.data.local.entities.ArchivoAdjuntoEntity
import com.example.notespote.data.local.entities.EstadoDescarga
import kotlinx.coroutines.flow.Flow


@Dao
interface ArchivoAdjuntoDao {
    @Query("SELECT * FROM archivo_adjunto WHERE id_apunte = :apunteId AND is_deleted = 0")
    fun getArchivosByApunte(apunteId: String): Flow<List<ArchivoAdjuntoEntity>>

    @Query("SELECT * FROM archivo_adjunto WHERE sync_status != 'SYNCED' AND is_deleted = 0")
    suspend fun getPendingSyncArchivos(): List<ArchivoAdjuntoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(archivo: ArchivoAdjuntoEntity)

    @Update
    suspend fun update(archivo: ArchivoAdjuntoEntity)

    @Query("UPDATE archivo_adjunto SET estado_descarga = :estado WHERE id_archivo = :id")
    suspend fun updateEstadoDescarga(id: String, estado: EstadoDescarga)

    @Query("DELETE FROM archivo_adjunto WHERE id_archivo = :id")
    suspend fun delete(id: String)
}
