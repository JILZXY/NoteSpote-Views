package com.example.notespote.data.remote.dto

import com.google.firebase.Timestamp

data class SeguimientoDto(
    val id: String = "",
    val idSeguidor: String = "",
    val idSeguido: String = "",
    val fechaSeguimiento: Timestamp = Timestamp.now(),
    val isDeleted: Boolean = false
) {
    constructor() : this("")
}