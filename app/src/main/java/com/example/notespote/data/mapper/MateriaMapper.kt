package com.example.notespote.data.mapper

import com.example.notespote.data.local.entities.MateriaEntity
import com.example.notespote.data.local.entities.SyncStatus
import com.example.notespote.data.remote.dto.MateriaDto
import com.example.notespote.domain.model.Materia
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MateriaMapper @Inject constructor() {

    fun toDomain(entity: MateriaEntity): Materia {
        return Materia(
            id = entity.idMateria,
            nombreMateria = entity.nombreMateria,
            categoria = entity.categoria,
            descripcion = entity.descripcion,
            icono = entity.icono
        )
    }

    fun toEntity(domain: Materia): MateriaEntity {
        return MateriaEntity(
            idMateria = domain.id,
            nombreMateria = domain.nombreMateria,
            categoria = domain.categoria,
            descripcion = domain.descripcion,
            icono = domain.icono,
            syncStatus = SyncStatus.PENDING_UPLOAD,
            isDeleted = false
        )
    }

    fun entityToDto(entity: MateriaEntity): MateriaDto {
        return MateriaDto(
            id = entity.idMateria,
            nombreMateria = entity.nombreMateria,
            categoria = entity.categoria,
            descripcion = entity.descripcion,
            icono = entity.icono,
            isDeleted = entity.isDeleted
        )
    }

    fun dtoToEntity(dto: MateriaDto): MateriaEntity {
        return MateriaEntity(
            idMateria = dto.id,
            nombreMateria = dto.nombreMateria,
            categoria = dto.categoria,
            descripcion = dto.descripcion,
            icono = dto.icono,
            syncStatus = SyncStatus.SYNCED,
            isDeleted = dto.isDeleted
        )
    }
}