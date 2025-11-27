package com.example.notespote.domain.model

data class SesionUsuario(
    val usuario: com.example.notespote.domain.model.Usuario,
    val token: String? = null,
    val estaAutenticado: Boolean = true,
    val ultimaActualizacion: Long = System.currentTimeMillis()
)