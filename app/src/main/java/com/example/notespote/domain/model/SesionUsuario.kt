package com.example.notespote.data.model

data class SesionUsuario(
    val usuario: Usuario,
    val token: String? = null,
    val estaAutenticado: Boolean = true,
    val ultimaActualizacion: Long = System.currentTimeMillis()
)