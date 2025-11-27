package com.example.notespote.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.notespote.data.local.entities.EtiquetaApunteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EtiquetaApunteDao {
    @Query("SELECT * FROM etiqueta_apunte WHERE id_apunte = :apunteId AND is_deleted = 0")
    fun getEtiquetasByApunte(apunteId: String): Flow<List<EtiquetaApunteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(etiquetaApunte: EtiquetaApunteEntity)

    @Query("DELETE FROM etiqueta_apunte WHERE id_etiqueta_apunte = :id")
    suspend fun delete(id: String)
}