package com.example.notespote.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
@Entity(
    tableName = "seguimiento",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_seguidor"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_seguido"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["id_seguidor", "id_seguido"], unique = true),
        Index(value = ["id_seguidor"]),
        Index(value = ["id_seguido"])
    ]
)
data class SeguimientoEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_seguimiento") val idSeguimiento: String,
    @ColumnInfo(name = "id_seguidor") val idSeguidor: String,
    @ColumnInfo(name = "id_seguido") val idSeguido: String,
    @ColumnInfo(name = "fecha_seguimiento") val fechaSeguimiento: Long,
    @ColumnInfo(name = "sync_status") val syncStatus: SyncStatus = SyncStatus.SYNCED,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false
)