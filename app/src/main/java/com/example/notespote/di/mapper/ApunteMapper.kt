package com.example.notespote.di.mapper

import com.example.notespote.data.local.entities.ApunteEntity
import com.example.notespote.data.remote.dto.ApunteDto
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