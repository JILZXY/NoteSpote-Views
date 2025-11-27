package com.example.notespote.data.mapper

import com.example.notespote.data.local.entities.SeguimientoEntity
import com.example.notespote.data.local.entities.SyncStatus
import com.example.notespote.data.remote.dto.SeguimientoDto
import com.example.notespote.domain.model.Seguimiento
import com.google.firebase.Timestamp
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeguimientoMapper @Inject constructor() {

    fun toDomain(entity: SeguimientoEntity): com.example.notespote.domain.model.Seguimiento {
        return _root_ide_package_.com.example.notespote.domain.model.Seguimiento(
            id = entity.idSeguimiento,
            idSeguidor = entity.idSeguidor,
            idSeguido = entity.idSeguido,
            fechaSeguimiento = entity.fechaSeguimiento
        )
    }

    fun toEntity(domain: com.example.notespote.domain.model.Seguimiento): SeguimientoEntity {
        return SeguimientoEntity(
            idSeguimiento = domain.id,
            idSeguidor = domain.idSeguidor,
            idSeguido = domain.idSeguido,
            fechaSeguimiento = domain.fechaSeguimiento,
            syncStatus = SyncStatus.PENDING_UPLOAD,
            isDeleted = false
        )
    }

    fun entityToDto(entity: SeguimientoEntity): SeguimientoDto {
        return SeguimientoDto(
            id = entity.idSeguimiento,
            idSeguidor = entity.idSeguidor,
            idSeguido = entity.idSeguido,
            fechaSeguimiento = Timestamp(Date(entity.fechaSeguimiento)),
            isDeleted = entity.isDeleted
        )
    }

    fun dtoToEntity(dto: SeguimientoDto): SeguimientoEntity {
        return SeguimientoEntity(
            idSeguimiento = dto.id,
            idSeguidor = dto.idSeguidor,
            idSeguido = dto.idSeguido,
            fechaSeguimiento = dto.fechaSeguimiento.toDate().time,
            syncStatus = SyncStatus.SYNCED,
            isDeleted = dto.isDeleted
        )
    }
}