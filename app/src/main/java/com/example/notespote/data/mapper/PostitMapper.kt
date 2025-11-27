package com.example.notespote.data.mapper

import com.example.notespote.data.local.entities.PostitEntity
import com.example.notespote.data.local.entities.SyncStatus
import com.example.notespote.data.remote.dto.PostitDto
import com.example.notespote.data.model.*
import com.google.firebase.Timestamp
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostitMapper @Inject constructor() {

    fun toDomain(entity: PostitEntity): com.example.notespote.domain.model.Postit {
        return _root_ide_package_.com.example.notespote.domain.model.Postit(
            id = entity.idPostit,
            idApunte = entity.idApunte,
            titulo = entity.tituloPostit,
            contenido = entity.contenidoPostit,
            color = entity.color,
            posicionX = entity.posicionX,
            posicionY = entity.posicionY,
            ancho = entity.ancho,
            alto = entity.alto,
            fechaCreacion = entity.fechaCreacion,
            ordenZ = entity.ordenZ
        )
    }

    fun toEntity(domain: com.example.notespote.domain.model.Postit): PostitEntity {
        return PostitEntity(
            idPostit = domain.id,
            idApunte = domain.idApunte,
            tituloPostit = domain.titulo,
            contenidoPostit = domain.contenido,
            color = domain.color,
            posicionX = domain.posicionX,
            posicionY = domain.posicionY,
            ancho = domain.ancho,
            alto = domain.alto,
            fechaCreacion = domain.fechaCreacion,
            ordenZ = domain.ordenZ,
            syncStatus = SyncStatus.PENDING_UPLOAD,
            isDeleted = false
        )
    }

    fun entityToDto(entity: PostitEntity): PostitDto {
        return PostitDto(
            id = entity.idPostit,
            tituloPostit = entity.tituloPostit,
            contenidoPostit = entity.contenidoPostit,
            color = entity.color,
            posicionX = entity.posicionX,
            posicionY = entity.posicionY,
            ancho = entity.ancho,
            alto = entity.alto,
            fechaCreacion = Timestamp(Date(entity.fechaCreacion)),
            ordenZ = entity.ordenZ,
            isDeleted = entity.isDeleted
        )
    }

    fun dtoToEntity(dto: PostitDto, idApunte: String): PostitEntity {
        return PostitEntity(
            idPostit = dto.id,
            idApunte = idApunte,
            tituloPostit = dto.tituloPostit,
            contenidoPostit = dto.contenidoPostit,
            color = dto.color,
            posicionX = dto.posicionX,
            posicionY = dto.posicionY,
            ancho = dto.ancho,
            alto = dto.alto,
            fechaCreacion = dto.fechaCreacion.toDate().time,
            ordenZ = dto.ordenZ,
            syncStatus = SyncStatus.SYNCED,
            isDeleted = dto.isDeleted
        )
    }
}