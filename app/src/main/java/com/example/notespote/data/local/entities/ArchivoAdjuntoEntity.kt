package com.example.notespote.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
@Entity(
    tableName = "archivo_adjunto",
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
data class ArchivoAdjuntoEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_archivo") val idArchivo: String,
    @ColumnInfo(name = "id_apunte") val idApunte: String,
    @ColumnInfo(name = "nombre_archivo") val nombreArchivo: String,
    @ColumnInfo(name = "tipo_archivo") val tipoArchivo: String,
    @ColumnInfo(name = "ruta_local") val rutaLocal: String?, // Ruta en internal storage
    @ColumnInfo(name = "url_firebase") val urlFirebase: String?, // URL de Firebase Storage
    @ColumnInfo(name = "tamano_kb") val tamanoKb: Int,
    @ColumnInfo(name = "fecha_subida") val fechaSubida: Long,
    @ColumnInfo(name = "estado_descarga") val estadoDescarga: EstadoDescarga = EstadoDescarga.NO_DESCARGADO,
    @ColumnInfo(name = "sync_status") val syncStatus: SyncStatus = SyncStatus.SYNCED,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false
)

enum class EstadoDescarga {
    NO_DESCARGADO,
    DESCARGANDO,
    DESCARGADO,
    ERROR
}