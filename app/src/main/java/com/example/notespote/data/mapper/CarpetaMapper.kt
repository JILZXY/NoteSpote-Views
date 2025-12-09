package com.example.notespote.data.mapper

import com.example.notespote.data.local.entities.CarpetaEntity
import com.example.notespote.data.local.entities.SyncStatus
import com.example.notespote.data.remote.dto.CarpetaDto
import com.example.notespote.domain.model.Carpeta
import com.google.firebase.Timestamp
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CarpetaMapper @Inject constructor() {

    fun toDomain(entity: CarpetaEntity): Carpeta {
        return Carpeta(
            id = entity.idCarpeta,
            idUsuario = entity.idUsuario,
            idMateria = entity.idMateria,
            idCarpetaPadre = entity.idCarpetaPadre,
            nombreCarpeta = entity.nombreCarpeta,
            colorCarpeta = entity.colorCarpeta,
            descripcion = entity.descripcion,
            fechaCreacion = entity.fechaCreacion,
            orden = entity.orden
        )
    }

    fun toEntity(domain: Carpeta): CarpetaEntity {
        return CarpetaEntity(
            idCarpeta = domain.id,
            idUsuario = domain.idUsuario,
            idMateria = domain.idMateria,
            idCarpetaPadre = domain.idCarpetaPadre,
            nombreCarpeta = domain.nombreCarpeta,
            colorCarpeta = domain.colorCarpeta,
            descripcion = domain.descripcion,
            fechaCreacion = domain.fechaCreacion,
            orden = domain.orden,
            syncStatus = SyncStatus.PENDING_UPLOAD,
            lastSync = null,
            isDeleted = false
        )
    }

    fun entityToDto(entity: CarpetaEntity): CarpetaDto {
        return CarpetaDto(
            id = entity.idCarpeta,
            idUsuario = entity.idUsuario,
            idMateria = entity.idMateria,
            idCarpetaPadre = entity.idCarpetaPadre,
            nombreCarpeta = entity.nombreCarpeta,
            colorCarpeta = entity.colorCarpeta,
            descripcion = entity.descripcion,
            fechaCreacion = Timestamp(Date(entity.fechaCreacion)),
            orden = entity.orden,
            updatedAt = Timestamp.now(),
            isDeleted = entity.isDeleted
        )
    }

    fun dtoToEntity(dto: CarpetaDto): CarpetaEntity {
        return CarpetaEntity(
            idCarpeta = dto.id,
            idUsuario = dto.idUsuario,
            idMateria = dto.idMateria,
            idCarpetaPadre = dto.idCarpetaPadre,
            nombreCarpeta = dto.nombreCarpeta,
            colorCarpeta = dto.colorCarpeta,
            descripcion = dto.descripcion,
            fechaCreacion = dto.fechaCreacion.toDate().time,
            orden = dto.orden,
            syncStatus = SyncStatus.SYNCED,
            lastSync = System.currentTimeMillis(),
            isDeleted = dto.isDeleted
        )
    }
}