package com.example.notespote.data.model

data class CarpetaContenido(
    val carpeta: Carpeta,
    val subcarpetas: List<Carpeta> = emptyList(),
    val apuntes: List<Apunte> = emptyList(),
    val totalItems: Int = subcarpetas.size + apuntes.size
)