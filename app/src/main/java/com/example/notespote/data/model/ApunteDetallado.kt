package com.example.notespote.data.model

data class ApunteDetallado(
    val apunte: Apunte,
    val postits: List<Postit> = emptyList(),
    val archivos: List<ArchivoAdjunto> = emptyList(),
    val etiquetas: List<Etiqueta> = emptyList()
)