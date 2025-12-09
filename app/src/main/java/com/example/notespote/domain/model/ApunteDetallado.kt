package com.example.notespote.domain.model

data class ApunteDetallado(
    val apunte: com.example.notespote.domain.model.Apunte,
    val postits: List<com.example.notespote.domain.model.Postit> = emptyList(),
    val archivos: List<com.example.notespote.domain.model.ArchivoAdjunto> = emptyList(),
    val etiquetas: List<com.example.notespote.domain.model.Etiqueta> = emptyList(),
    val autor: com.example.notespote.domain.model.Usuario? = null
)