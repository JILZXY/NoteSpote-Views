package com.example.notespote.data.model

data class Seguimiento(
    val id: String = "",
    val idSeguidor: String = "",
    val idSeguido: String = "",
    val fechaSeguimiento: Long = System.currentTimeMillis()
)