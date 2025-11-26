package com.example.notespote.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "usuario",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["nombre_usuario"], unique = true)
    ]
)
data class UsuarioEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_usuario") val idUsuario: String, // UUID para sincronización
    @ColumnInfo(name = "nombre") val nombre: String?,
    @ColumnInfo(name = "apellido") val apellido: String?,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "password") val password: ByteArray?, // Solo local
    @ColumnInfo(name = "nombre_usuario") val nombreUsuario: String,
    @ColumnInfo(name = "foto_perfil") val fotoPerfil: String?, // Ruta local o URL
    @ColumnInfo(name = "biografia") val biografia: String?,
    @ColumnInfo(name = "fecha_registro") val fechaRegistro: Long,
    @ColumnInfo(name = "ultima_conexion") val ultimaConexion: Long?,
    @ColumnInfo(name = "total_likes_recibidos") val totalLikesRecibidos: Int = 0,
    @ColumnInfo(name = "total_seguidores") val totalSeguidores: Int = 0,
    @ColumnInfo(name = "total_seguidos") val totalSeguidos: Int = 0,

    @ColumnInfo(name = "sync_status") val syncStatus: SyncStatus = SyncStatus.SYNCED,
    @ColumnInfo(name = "last_sync") val lastSync: Long? = null,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false
)

enum class SyncStatus {
    SYNCED,
    PENDING_UPLOAD,
    PENDING_UPDATE,
    PENDING_DELETE,
    CONFLICT
}