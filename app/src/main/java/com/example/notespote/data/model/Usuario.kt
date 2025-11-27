package com.example.notespote.data.model

data class Usuario(
    val id: String = "",
    val nombre: String? = null,
    val apellido: String? = null,
    val email: String = "",
    val nombreUsuario: String = "",
    val fotoPerfil: String? = null,
    val biografia: String? = null,
    val fechaRegistro: Long = System.currentTimeMillis(),
    val ultimaConexion: Long? = null,
    val totalLikesRecibidos: Int = 0,
    val totalSeguidores: Int = 0,
    val totalSeguidos: Int = 0
)