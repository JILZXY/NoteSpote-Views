package com.example.notespote.data.remote.dto

data class EtiquetaDto(
    val id: String = "",
    val nombreEtiqueta: String = "",
    val vecesUsada: Int = 0
) {
    constructor() : this("")
}