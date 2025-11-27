package com.example.notespote.data.model

import com.example.notespote.data.local.entities.TipoVisibilidad

data class Apunte(
    val id: String = "",
    val idUsuario: String = "",
    val idMateria: String? = null,
    val idCarpeta: String? = null,
    val titulo: String = "",
    val contenido: String? = null,
    val tipoVisibilidad: TipoVisibilidad = TipoVisibilidad.PRIVADO,
    val esOriginal: Boolean = true,
    val idApunteOriginal: String? = null,
    val idUsuarioOriginal: String? = null,
    val totalLikes: Int = 0,
    val totalGuardados: Int = 0,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val fechaActualizacion: Long? = null,
    val tieneDibujos: Boolean = false,
    val tieneImagenes: Boolean = false,
    val tienePostits: Boolean = false
)