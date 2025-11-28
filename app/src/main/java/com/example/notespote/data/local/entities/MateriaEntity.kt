package com.example.notespote.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "materia",
    indices = [Index(value = ["nombre_materia"])]
)
data class MateriaEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_materia") val idMateria: String,
    @ColumnInfo(name = "nombre_materia") val nombreMateria: String,
    @ColumnInfo(name = "categoria") val categoria: String?,
    @ColumnInfo(name = "descripcion") val descripcion: String?,
    @ColumnInfo(name = "icono") val icono: String?,
    @ColumnInfo(name = "sync_status") val syncStatus: SyncStatus = SyncStatus.SYNCED,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false
)