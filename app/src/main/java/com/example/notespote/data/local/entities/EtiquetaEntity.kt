package com.example.notespote.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "etiqueta",
    indices = [Index(value = ["nombre_etiqueta"], unique = true)]
)
data class EtiquetaEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_etiqueta") val idEtiqueta: String,
    @ColumnInfo(name = "nombre_etiqueta") val nombreEtiqueta: String,
    @ColumnInfo(name = "veces_usada") val vecesUsada: Int = 0,
    @ColumnInfo(name = "sync_status") val syncStatus: SyncStatus = SyncStatus.SYNCED
)