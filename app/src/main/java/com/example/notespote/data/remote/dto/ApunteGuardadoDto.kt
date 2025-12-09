package com.example.notespote.data.remote.dto

import com.google.firebase.Timestamp

data class ApunteGuardadoDto(
    val id: String = "", // Formato: {userId}__{apunteId}
    val idUsuario: String = "",
    val idApunte: String = "",
    val idUsuarioOriginal: String = "",
    val fechaGuardado: Timestamp = Timestamp.now(),
    val isDeleted: Boolean = false
) {
    constructor() : this("")
}