package com.example.notespote.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "apunte",
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
            childColumns = ["id_carpeta"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["id_usuario"]),
        Index(value = ["id_materia"]),
        Index(value = ["id_carpeta"]),
        Index(value = ["tipo_visibilidad"]),
        Index(value = ["fecha_creacion"])
    ]
)
data class ApunteEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_apunte") val idApunte: String,
    @ColumnInfo(name = "id_usuario") val idUsuario: String,
    @ColumnInfo(name = "id_materia") val idMateria: String?,
    @ColumnInfo(name = "id_carpeta") val idCarpeta: String?,
    @ColumnInfo(name = "titulo") val titulo: String,
    @ColumnInfo(name = "contenido") val contenido: String?,
    @ColumnInfo(name = "tipo_visibilidad") val tipoVisibilidad: TipoVisibilidad,
    @ColumnInfo(name = "es_original") val esOriginal: Boolean = true,
    @ColumnInfo(name = "id_apunte_original") val idApunteOriginal: String?,
    @ColumnInfo(name = "id_usuario_original") val idUsuarioOriginal: String?, // Para guardados
    @ColumnInfo(name = "total_likes") val totalLikes: Int = 0,
    @ColumnInfo(name = "total_guardados") val totalGuardados: Int = 0,
    @ColumnInfo(name = "fecha_creacion") val fechaCreacion: Long,
    @ColumnInfo(name = "fecha_actualizacion") val fechaActualizacion: Long?,
    @ColumnInfo(name = "tiene_dibujos") val tieneDibujos: Boolean = false,
    @ColumnInfo(name = "tiene_imagenes") val tieneImagenes: Boolean = false,
    @ColumnInfo(name = "tiene_postits") val tienePostits: Boolean = false,
    @ColumnInfo(name = "sync_status") val syncStatus: SyncStatus = SyncStatus.SYNCED,
    @ColumnInfo(name = "last_sync") val lastSync: Long? = null,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false
)

enum class TipoVisibilidad {
    PUBLICO,
    PRIVADO,
    SOLO_AMIGOS
}