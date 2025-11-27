package com.example.notespote.domain.model

data class ResultadoBusqueda(
    val apuntes: List<com.example.notespote.domain.model.Apunte> = emptyList(),
    val usuarios: List<com.example.notespote.domain.model.Usuario> = emptyList(),
    val carpetas: List<com.example.notespote.domain.model.Carpeta> = emptyList(),
    val etiquetas: List<com.example.notespote.domain.model.Etiqueta> = emptyList()
)