package com.example.notespote.data.mapper

import com.example.notespote.data.local.entities.PostitEntity
import com.google.firebase.Timestamp
import java.util.Date

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