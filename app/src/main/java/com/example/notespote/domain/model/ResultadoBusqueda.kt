package com.example.notespote.data.model

data class ResultadoBusqueda(
    val apuntes: List<Apunte> = emptyList(),
    val usuarios: List<Usuario> = emptyList(),
    val carpetas: List<Carpeta> = emptyList(),
    val etiquetas: List<Etiqueta> = emptyList()
)