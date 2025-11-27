package com.example.notespote.di.remote.dto

import com.google.firebase.Timestamp

data class UsuarioDto(
    val id: String = "",
    val nombre: String? = null,
    val apellido: String? = null,
    val email: String = "",
    val nombreUsuario: String = "",
    val fotoPerfilUrl: String? = null,
    val biografia: String? = null,
    val fechaRegistro: Timestamp = Timestamp.now(),
    val ultimaConexion: Timestamp? = null,
    val totalLikesRecibidos: Int = 0,
    val totalSeguidores: Int = 0,
    val totalSeguidos: Int = 0,
    val updatedAt: Timestamp = Timestamp.now(),
    val isDeleted: Boolean = false
) {
    constructor() : this("")
}