package com.example.notespote.data.remote.dto

import com.google.firebase.Timestamp

data class ReporteDto(
    val id: String = "",
    val idUsuarioReportador: String = "",
    val tipoContenido: String = "", // APUNTE, USUARIO, COMENTARIO
    val idContenido: String = "",
    val motivo: String = "",
    val descripcion: String? = null,
    val estado: String = "PENDIENTE", // PENDIENTE, REVISADO, RESUELTO
    val fecha: Timestamp = Timestamp.now()
) {
    constructor() : this("")
}
