package com.example.notespote.domain.model

data class CarpetaContenido(
    val carpeta: com.example.notespote.domain.model.Carpeta,
    val subcarpetas: List<com.example.notespote.domain.model.Carpeta> = emptyList(),
    val apuntes: List<com.example.notespote.domain.model.Apunte> = emptyList(),
    val totalItems: Int = subcarpetas.size + apuntes.size
)