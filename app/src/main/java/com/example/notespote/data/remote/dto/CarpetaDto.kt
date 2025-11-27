package com.example.notespote.data.remote.dto

import com.google.firebase.Timestamp

data class CarpetaDto(
    val id: String = "",
    val idUsuario: String = "",
    val idMateria: String? = null,
    val idCarpetaPadre: String? = null,
    val nombreCarpeta: String = "",
    val colorCarpeta: String? = null,
    val descripcion: String? = null,
    val fechaCreacion: Timestamp = Timestamp.now(),
    val orden: Int = 0,
    val updatedAt: Timestamp = Timestamp.now(),
    val isDeleted: Boolean = false
) {
    constructor() : this("")
}