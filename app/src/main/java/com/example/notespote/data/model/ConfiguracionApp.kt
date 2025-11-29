package com.example.notespote.data.model

data class ConfiguracionApp(
    val modoOscuro: Boolean = false,
    val sincronizacionAutomatica: Boolean = true,
    val notificacionesActivadas: Boolean = true,
    val descargarSoloWifi: Boolean = true,
    val idioma: String = "es"
)