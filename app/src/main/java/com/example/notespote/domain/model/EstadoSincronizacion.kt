package com.example.notespote.domain.model

data class EstadoSincronizacion(
    val sincronizando: Boolean = false,
    val ultimaSincronizacion: Long? = null,
    val itemsPendientes: Int = 0,
    val itemsSincronizados: Int = 0,
    val errores: List<com.example.notespote.domain.model.ErrorSincronizacion> = emptyList()
)

data class ErrorSincronizacion(
    val tipo: String,
    val mensaje: String,
    val timestamp: Long = System.currentTimeMillis()
)