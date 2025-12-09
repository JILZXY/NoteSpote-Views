package com.example.notespote.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "postit",
    foreignKeys = [
        ForeignKey(
            entity = ApunteEntity::class,
            parentColumns = ["id_apunte"],
            childColumns = ["id_apunte"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["id_apunte"])]
)
data class PostitEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_postit") val idPostit: String,
    @ColumnInfo(name = "id_apunte") val idApunte: String,
    @ColumnInfo(name = "titulo_postit") val tituloPostit: String?,
    @ColumnInfo(name = "contenido_postit") val contenidoPostit: String?,
    @ColumnInfo(name = "color") val color: String,
    @ColumnInfo(name = "posicion_x") val posicionX: Int,
    @ColumnInfo(name = "posicion_y") val posicionY: Int,
    @ColumnInfo(name = "ancho") val ancho: Int,
    @ColumnInfo(name = "alto") val alto: Int,
    @ColumnInfo(name = "fecha_creacion") val fechaCreacion: Long,
    @ColumnInfo(name = "orden_z") val ordenZ: Int,
    @ColumnInfo(name = "sync_status") val syncStatus: SyncStatus = SyncStatus.SYNCED,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false
)