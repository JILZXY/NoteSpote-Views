package com.example.notespote.data.remote.dto

data class ConfiguracionDto(
    val idUsuario: String = "",
    val modoOscuro: Boolean = false,
    val sincronizacionAutomatica: Boolean = true,
    val notificacionesActivadas: Boolean = true,
    val descargarSoloWifi: Boolean = true,
    val idioma: String = "es",
    val updatedAt: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()
) {
    constructor() : this("")
}