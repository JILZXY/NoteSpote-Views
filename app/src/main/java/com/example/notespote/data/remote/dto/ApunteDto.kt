package com.example.notespote.data.remote.dto

import com.google.firebase.Timestamp

data class ApunteDto(
    val id: String = "",
    val idUsuario: String = "",
    val idMateria: String? = null,
    val idCarpeta: String? = null,
    val titulo: String = "",
    val contenido: String? = null,
    val tipoVisibilidad: String = "PRIVADO",
    val esOriginal: Boolean = true,
    val idApunteOriginal: String? = null,
    val idUsuarioOriginal: String? = null,
    val totalLikes: Int = 0,
    val totalGuardados: Int = 0,
    val fechaCreacion: Timestamp = Timestamp.now(),
    val fechaActualizacion: Timestamp? = null,
    val tieneDibujos: Boolean = false,
    val tieneImagenes: Boolean = false,
    val tienePostits: Boolean = false,
    val updatedAt: Timestamp = Timestamp.now(),
    val isDeleted: Boolean = false
) {
    constructor() : this("")
}