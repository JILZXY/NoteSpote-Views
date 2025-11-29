package com.example.notespote.data.mapper

import com.example.notespote.data.local.entities.*
import com.example.notespote.data.remote.dto.*
import com.google.firebase.Timestamp
import java.util.Date

fun ApunteEntity.toDto(): ApunteDto {
    return ApunteDto(
        id = idApunte,
        idUsuario = idUsuario,
        idMateria = idMateria,
        idCarpeta = idCarpeta,
        titulo = titulo,
        contenido = contenido,
        tipoVisibilidad = tipoVisibilidad.name,
        esOriginal = esOriginal,
        idApunteOriginal = idApunteOriginal,
        idUsuarioOriginal = idUsuarioOriginal,
        totalLikes = totalLikes,
        totalGuardados = totalGuardados,
        fechaCreacion = Timestamp(Date(fechaCreacion)),
        fechaActualizacion = fechaActualizacion?.let { Timestamp(Date(it)) },
        tieneDibujos = tieneDibujos,
        tieneImagenes = tieneImagenes,
        tienePostits = tienePostits,
        updatedAt = Timestamp.now(),
        isDeleted = isDeleted
    )
}

fun ApunteDto.toEntity(): ApunteEntity {
    return ApunteEntity(
        idApunte = id,
        idUsuario = idUsuario,
        idMateria = idMateria,
        idCarpeta = idCarpeta,
        titulo = titulo,
        contenido = contenido,
        tipoVisibilidad = TipoVisibilidad.valueOf(tipoVisibilidad),
        esOriginal = esOriginal,
        idApunteOriginal = idApunteOriginal,
        idUsuarioOriginal = idUsuarioOriginal,
        totalLikes = totalLikes,
        totalGuardados = totalGuardados,
        fechaCreacion = fechaCreacion.toDate().time,
        fechaActualizacion = fechaActualizacion?.toDate()?.time,
        tieneDibujos = tieneDibujos,
        tieneImagenes = tieneImagenes,
        tienePostits = tienePostits,
        syncStatus = SyncStatus.SYNCED,
        lastSync = System.currentTimeMillis(),
        isDeleted = isDeleted
    )
}

fun PostitEntity.toDto(): PostitDto {
    return PostitDto(
        id = idPostit,
        tituloPostit = tituloPostit,
        contenidoPostit = contenidoPostit,
        color = color,
        posicionX = posicionX,
        posicionY = posicionY,
        ancho = ancho,
        alto = alto,
        fechaCreacion = Timestamp(Date(fechaCreacion)),
        ordenZ = ordenZ,
        isDeleted = isDeleted
    )
}