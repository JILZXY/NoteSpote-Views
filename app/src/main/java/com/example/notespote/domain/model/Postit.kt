package com.example.notespote.domain.model

// domain/model/Postit.kt
data class Postit(
    val id: String = "",
    val idApunte: String = "",
    val titulo: String? = null,
    val contenido: String? = null,
    val color: String = "#FFEB3B",
    val posicionX: Int = 0,
    val posicionY: Int = 0,
    val ancho: Int = 200,
    val alto: Int = 200,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val ordenZ: Int = 0
)