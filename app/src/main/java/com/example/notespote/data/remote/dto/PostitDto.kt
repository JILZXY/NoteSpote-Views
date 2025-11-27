package com.example.notespote.data.remote.dto

import com.google.firebase.Timestamp

data class PostitDto(
    val id: String = "",
    val tituloPostit: String? = null,
    val contenidoPostit: String? = null,
    val color: String = "",
    val posicionX: Int = 0,
    val posicionY: Int = 0,
    val ancho: Int = 0,
    val alto: Int = 0,
    val fechaCreacion: Timestamp = Timestamp.now(),
    val ordenZ: Int = 0,
    val isDeleted: Boolean = false
) {
    constructor() : this("")
}