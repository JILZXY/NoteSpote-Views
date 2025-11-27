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
) {
    val tamanoMB: Float
        get() = tamanoKb / 1024f

    val extension: String
        get() = nombreArchivo.substringAfterLast('.', "")

    val esImagen: Boolean
        get() = tipoArchivo.startsWith("image/")

    val esPDF: Boolean
        get() = tipoArchivo == "application/pdf"

    val esDocumento: Boolean
        get() = tipoArchivo.contains("document") ||
                tipoArchivo.contains("text") ||
                extension in listOf("doc", "docx", "txt")
}

enum class EstadoDescarga {
    NO_DESCARGADO,
    DESCARGANDO,
    DESCARGADO,
    ERROR
}