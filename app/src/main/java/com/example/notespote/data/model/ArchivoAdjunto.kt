package com.example.notespote.data.model

import com.example.notespote.data.local.entities.EstadoDescarga

// domain/model/ArchivoAdjunto.kt
data class ArchivoAdjunto(
    val id: String = "",
    val idApunte: String = "",
    val nombreArchivo: String = "",
    val tipoArchivo: String = "",
    val rutaLocal: String? = null,
    val urlFirebase: String? = null,
    val tamanoKb: Int = 0,
    val fechaSubida: Long = System.currentTimeMillis(),
    val estadoDescarga: EstadoDescarga = EstadoDescarga.NO_DESCARGADO
)