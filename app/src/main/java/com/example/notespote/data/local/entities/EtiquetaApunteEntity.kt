package com.example.notespote.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "etiqueta_apunte",
    foreignKeys = [
        ForeignKey(
            entity = ApunteEntity::class,
            parentColumns = ["id_apunte"],
            childColumns = ["id_apunte"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EtiquetaEntity::class,
            parentColumns = ["id_etiqueta"],
            childColumns = ["id_etiqueta"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["id_apunte", "id_etiqueta"], unique = true),
        Index(value = ["id_apunte"]),
        Index(value = ["id_etiqueta"])
    ]
)
data class EtiquetaApunteEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_etiqueta_apunte") val idEtiquetaApunte: String,
    @ColumnInfo(name = "id_apunte") val idApunte: String,
    @ColumnInfo(name = "id_etiqueta") val idEtiqueta: String,
    @ColumnInfo(name = "sync_status") val syncStatus: SyncStatus = SyncStatus.SYNCED,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false
)