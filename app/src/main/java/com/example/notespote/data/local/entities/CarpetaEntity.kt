package com.example.notespote.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "carpeta",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_usuario"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MateriaEntity::class,
            parentColumns = ["id_materia"],
            childColumns = ["id_materia"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = CarpetaEntity::class,
            parentColumns = ["id_carpeta"],
            childColumns = ["id_carpeta_padre"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["id_usuario"]),
        Index(value = ["id_materia"]),
        Index(value = ["id_carpeta_padre"])
    ]
)
data class CarpetaEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_carpeta") val idCarpeta: String,
    @ColumnInfo(name = "id_usuario") val idUsuario: String,
    @ColumnInfo(name = "id_materia") val idMateria: String?,
    @ColumnInfo(name = "id_carpeta_padre") val idCarpetaPadre: String?,
    @ColumnInfo(name = "nombre_carpeta") val nombreCarpeta: String,
    @ColumnInfo(name = "color_carpeta") val colorCarpeta: String?,
    @ColumnInfo(name = "descripcion") val descripcion: String?,
    @ColumnInfo(name = "fecha_creacion") val fechaCreacion: Long,
    @ColumnInfo(name = "orden") val orden: Int = 0,
    @ColumnInfo(name = "sync_status") val syncStatus: SyncStatus = SyncStatus.SYNCED,
    @ColumnInfo(name = "last_sync") val lastSync: Long? = null,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false
)