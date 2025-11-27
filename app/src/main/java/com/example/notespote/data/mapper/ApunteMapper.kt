package com.example.notespote.data.mapper

import com.example.notespote.data.local.dao.ApunteWithDetails
import com.example.notespote.data.local.entities.*
import com.example.notespote.data.remote.dto.ApunteDto
import com.example.notespote.data.model.*
import com.google.firebase.Timestamp
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApunteMapper @Inject constructor() {

    fun toDomain(entity: ApunteEntity): com.example.notespote.domain.model.Apunte {
        return _root_ide_package_.com.example.notespote.domain.model.Apunte(
            id = entity.idApunte,
            idUsuario = entity.idUsuario,
            idMateria = entity.idMateria,
            idCarpeta = entity.idCarpeta,
            titulo = entity.titulo,
            contenido = entity.contenido,
            tipoVisibilidad = entity.tipoVisibilidad,
            esOriginal = entity.esOriginal,
            idApunteOriginal = entity.idApunteOriginal,
            idUsuarioOriginal = entity.idUsuarioOriginal,
            totalLikes = entity.totalLikes,
            totalGuardados = entity.totalGuardados,
            fechaCreacion = entity.fechaCreacion,
            fechaActualizacion = entity.fechaActualizacion,
            tieneDibujos = entity.tieneDibujos,
            tieneImagenes = entity.tieneImagenes,
            tienePostits = entity.tienePostits
        )
    }

    fun toEntity(domain: com.example.notespote.domain.model.Apunte): ApunteEntity {
        return ApunteEntity(
            idApunte = domain.id,
            idUsuario = domain.idUsuario,
            idMateria = domain.idMateria,
            idCarpeta = domain.idCarpeta,
            titulo = domain.titulo,
            contenido = domain.contenido,
            tipoVisibilidad = domain.tipoVisibilidad,
            esOriginal = domain.esOriginal,
            idApunteOriginal = domain.idApunteOriginal,
            idUsuarioOriginal = domain.idUsuarioOriginal,
            totalLikes = domain.totalLikes,
            totalGuardados = domain.totalGuardados,
            fechaCreacion = domain.fechaCreacion,
            fechaActualizacion = domain.fechaActualizacion,
            tieneDibujos = domain.tieneDibujos,
            tieneImagenes = domain.tieneImagenes,
            tienePostits = domain.tienePostits,
            syncStatus = SyncStatus.PENDING_UPLOAD,
            lastSync = null,
            isDeleted = false
        )
    }

    fun entityToDto(entity: ApunteEntity): ApunteDto {
        return ApunteDto(
            id = entity.idApunte,
            idUsuario = entity.idUsuario,
            idMateria = entity.idMateria,
            idCarpeta = entity.idCarpeta,
            titulo = entity.titulo,
            contenido = entity.contenido,
            tipoVisibilidad = entity.tipoVisibilidad.name,
            esOriginal = entity.esOriginal,
            idApunteOriginal = entity.idApunteOriginal,
            idUsuarioOriginal = entity.idUsuarioOriginal,
            totalLikes = entity.totalLikes,
            totalGuardados = entity.totalGuardados,
            fechaCreacion = Timestamp(Date(entity.fechaCreacion)),
            fechaActualizacion = entity.fechaActualizacion?.let { Timestamp(Date(it)) },
            tieneDibujos = entity.tieneDibujos,
            tieneImagenes = entity.tieneImagenes,
            tienePostits = entity.tienePostits,
            updatedAt = Timestamp.now(),
            isDeleted = entity.isDeleted
        )
    }

    fun dtoToEntity(dto: ApunteDto): ApunteEntity {
        return ApunteEntity(
            idApunte = dto.id,
            idUsuario = dto.idUsuario,
            idMateria = dto.idMateria,
            idCarpeta = dto.idCarpeta,
            titulo = dto.titulo,
            contenido = dto.contenido,
            tipoVisibilidad = TipoVisibilidad.valueOf(dto.tipoVisibilidad),
            esOriginal = dto.esOriginal,
            idApunteOriginal = dto.idApunteOriginal,
            idUsuarioOriginal = dto.idUsuarioOriginal,
            totalLikes = dto.totalLikes,
            totalGuardados = dto.totalGuardados,
            fechaCreacion = dto.fechaCreacion.toDate().time,
            fechaActualizacion = dto.fechaActualizacion?.toDate()?.time,
            tieneDibujos = dto.tieneDibujos,
            tieneImagenes = dto.tieneImagenes,
            tienePostits = dto.tienePostits,
            syncStatus = SyncStatus.SYNCED,
            lastSync = System.currentTimeMillis(),
            isDeleted = dto.isDeleted
        )
    }

    fun toApunteDetallado(
        apunteWithDetails: ApunteWithDetails,
        postitMapper: PostitMapper,
        archivoMapper: ArchivoAdjuntoMapper,
        etiquetaMapper: EtiquetaMapper
    ): com.example.notespote.domain.model.ApunteDetallado {
        return _root_ide_package_.com.example.notespote.domain.model.ApunteDetallado(
            apunte = toDomain(apunteWithDetails.apunte),
            postits = apunteWithDetails.postits.map { postitMapper.toDomain(it) },
            archivos = apunteWithDetails.archivos.map { archivoMapper.toDomain(it) },
            etiquetas = apunteWithDetails.etiquetas.map {
                etiquetaMapper.toDomain(it.etiqueta)
            }
        )
    }
}