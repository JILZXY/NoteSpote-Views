package com.example.notespote.data.remote.dto

import com.google.firebase.Timestamp

data class NotificacionDto(
    val id: String = "",
    val idUsuario: String = "",
    val tipo: String = "",
    val titulo: String = "",
    val mensaje: String = "",
    val idReferencia: String? = null,
    val leida: Boolean = false,
    val fecha: Timestamp = Timestamp.now(),
    val isDeleted: Boolean = false
) {
    constructor() : this("")
}