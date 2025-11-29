package com.example.notespote.data.remote.dto

import com.google.firebase.Timestamp

data class ActividadDto(
    val id: String = "",
    val idUsuario: String = "",
    val tipo: String = "", // CREAR_APUNTE, LIKE, SEGUIR, GUARDAR
    val descripcion: String = "",
    val idReferencia: String? = null,
    val fecha: Timestamp = Timestamp.now(),
    val esPublica: Boolean = true
) {
    constructor() : this("")
}