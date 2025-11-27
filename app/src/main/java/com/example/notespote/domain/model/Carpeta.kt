package com.example.notespote.data.model

data class Carpeta(
    val id: String = "",
    val idUsuario: String = "",
    val idMateria: String? = null,
    val idCarpetaPadre: String? = null,
    val nombreCarpeta: String = "",
    val colorCarpeta: String? = null,
    val descripcion: String? = null,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val orden: Int = 0
)