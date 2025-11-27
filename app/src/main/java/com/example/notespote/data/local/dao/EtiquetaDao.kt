package com.example.notespote.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.notespote.data.local.entities.EtiquetaEntity


@Dao
interface EtiquetaDao {
    @Query("SELECT * FROM etiqueta WHERE id_etiqueta = :id")
    suspend fun getEtiquetaById(id: String): EtiquetaEntity?

    @Query("SELECT * FROM etiqueta WHERE nombre_etiqueta = :nombre")
    suspend fun getEtiquetaByNombre(nombre: String): EtiquetaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(etiqueta: EtiquetaEntity)

    @Query("UPDATE etiqueta SET veces_usada = veces_usada + 1 WHERE id_etiqueta = :id")
    suspend fun incrementarUso(id: String)
}