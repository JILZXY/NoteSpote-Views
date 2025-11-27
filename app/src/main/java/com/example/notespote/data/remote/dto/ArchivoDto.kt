package com.example.notespote.data.remote.dto

import com.google.firebase.Timestamp

data class ArchivoDto(
    val id: String = "",
    val nombreArchivo: String = "",
    val tipoArchivo: String = "",
    val urlFirebase: String = "",
    val tamanoKb: Int = 0,
    val fechaSubida: Timestamp = Timestamp.now(),
    val isDeleted: Boolean = false
) {
    constructor() : this("")
}