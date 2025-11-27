package com.example.notespote.data.mapper

import com.example.notespote.data.local.entities.LikeEntity
import com.example.notespote.data.local.entities.SyncStatus
import com.example.notespote.data.remote.dto.LikeDto
import com.example.notespote.data.model.Like
import com.google.firebase.Timestamp
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LikeMapper @Inject constructor() {

    fun toDomain(entity: LikeEntity): Like {
        return Like(
            id = entity.idLike,
            idUsuario = entity.idUsuario,
            idApunte = entity.idApunte,
            fechaLike = entity.fechaLike
        )
    }

    fun toEntity(domain: Like): LikeEntity {
        return LikeEntity(
            idLike = domain.id,
            idUsuario = domain.idUsuario,
            idApunte = domain.idApunte,
            fechaLike = domain.fechaLike,
            syncStatus = SyncStatus.PENDING_UPLOAD,
            isDeleted = false
        )
    }

    fun entityToDto(entity: LikeEntity): LikeDto {
        return LikeDto(
            id = entity.idLike,
            idUsuario = entity.idUsuario,
            idApunte = entity.idApunte,
            fechaLike = Timestamp(Date(entity.fechaLike)),
            isDeleted = entity.isDeleted
        )
    }

    fun dtoToEntity(dto: LikeDto): LikeEntity {
        return LikeEntity(
            idLike = dto.id,
            idUsuario = dto.idUsuario,
            idApunte = dto.idApunte,
            fechaLike = dto.fechaLike.toDate().time,
            syncStatus = SyncStatus.SYNCED,
            isDeleted = dto.isDeleted
        )
    }
}