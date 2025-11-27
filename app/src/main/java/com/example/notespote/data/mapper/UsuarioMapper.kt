package com.example.notespote.data.mapper

import com.example.notespote.data.local.entities.SyncStatus
import com.example.notespote.data.local.entities.UsuarioEntity
import com.example.notespote.data.remote.dto.UsuarioDto
import com.example.notespote.data.model.Usuario
import com.google.firebase.Timestamp
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsuarioMapper @Inject constructor() {

    fun toDomain(entity: UsuarioEntity): Usuario {
        return Usuario(
            id = entity.idUsuario,
            nombre = entity.nombre,
            apellido = entity.apellido,
            email = entity.email,
            nombreUsuario = entity.nombreUsuario,
            fotoPerfil = entity.fotoPerfil,
            biografia = entity.biografia,
            fechaRegistro = entity.fechaRegistro,
            ultimaConexion = entity.ultimaConexion,
            totalLikesRecibidos = entity.totalLikesRecibidos,
            totalSeguidores = entity.totalSeguidores,
            totalSeguidos = entity.totalSeguidos
        )
    }

    fun toEntity(domain: Usuario): UsuarioEntity {
        return UsuarioEntity(
            idUsuario = domain.id,
            nombre = domain.nombre,
            apellido = domain.apellido,
            email = domain.email,
            password = null, // No se guarda la contraseña en domain
            nombreUsuario = domain.nombreUsuario,
            fotoPerfil = domain.fotoPerfil,
            biografia = domain.biografia,
            fechaRegistro = domain.fechaRegistro,
            ultimaConexion = domain.ultimaConexion,
            totalLikesRecibidos = domain.totalLikesRecibidos,
            totalSeguidores = domain.totalSeguidores,
            totalSeguidos = domain.totalSeguidos,
            syncStatus = SyncStatus.PENDING_UPLOAD,
            lastSync = null,
            isDeleted = false
        )
    }

    fun entityToDto(entity: UsuarioEntity): UsuarioDto {
        return UsuarioDto(
            id = entity.idUsuario,
            nombre = entity.nombre,
            apellido = entity.apellido,
            email = entity.email,
            nombreUsuario = entity.nombreUsuario,
            fotoPerfilUrl = entity.fotoPerfil,
            biografia = entity.biografia,
            fechaRegistro = Timestamp(Date(entity.fechaRegistro)),
            ultimaConexion = entity.ultimaConexion?.let { Timestamp(Date(it)) },
            totalLikesRecibidos = entity.totalLikesRecibidos,
            totalSeguidores = entity.totalSeguidores,
            totalSeguidos = entity.totalSeguidos,
            updatedAt = Timestamp.now(),
            isDeleted = entity.isDeleted
        )
    }

    fun dtoToEntity(dto: UsuarioDto): UsuarioEntity {
        return UsuarioEntity(
            idUsuario = dto.id,
            nombre = dto.nombre,
            apellido = dto.apellido,
            email = dto.email,
            password = null,
            nombreUsuario = dto.nombreUsuario,
            fotoPerfil = dto.fotoPerfilUrl,
            biografia = dto.biografia,
            fechaRegistro = dto.fechaRegistro.toDate().time,
            ultimaConexion = dto.ultimaConexion?.toDate()?.time,
            totalLikesRecibidos = dto.totalLikesRecibidos,
            totalSeguidores = dto.totalSeguidores,
            totalSeguidos = dto.totalSeguidos,
            syncStatus = SyncStatus.SYNCED,
            lastSync = System.currentTimeMillis(),
            isDeleted = dto.isDeleted
        )
    }
}