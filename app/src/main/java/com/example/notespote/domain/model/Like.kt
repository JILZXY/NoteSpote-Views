package com.example.notespote.domain.model

data class Like(
    val id: String = "",
    val idUsuario: String = "",
    val idApunte: String = "",
    val fechaLike: Long = System.currentTimeMillis()
)