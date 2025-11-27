package com.example.notespote.domain.model

data class Seguimiento(
    val id: String = "",
    val idSeguidor: String = "",
    val idSeguido: String = "",
    val fechaSeguimiento: Long = System.currentTimeMillis()
)