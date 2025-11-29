package com.example.notespote.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
@Entity(
    tableName = "like_table",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_usuario"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ApunteEntity::class,
            parentColumns = ["id_apunte"],
            childColumns = ["id_apunte"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["id_usuario", "id_apunte"], unique = true),
        Index(value = ["id_usuario"]),
        Index(value = ["id_apunte"])
    ]
)
data class LikeEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_like") val idLike: String,
    @ColumnInfo(name = "id_usuario") val idUsuario: String,
    @ColumnInfo(name = "id_apunte") val idApunte: String,
    @ColumnInfo(name = "fecha_like") val fechaLike: Long,
    @ColumnInfo(name = "sync_status") val syncStatus: SyncStatus = SyncStatus.SYNCED,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false
)