package com.example.notespote.data.remote.dto

data class EstadisticasDto(
    val idUsuario: String = "",
    val totalApuntes: Int = 0,
    val totalApuntesPublicos: Int = 0,
    val totalApuntesPrivados: Int = 0,
    val totalLikesRecibidos: Int = 0,
    val totalSeguidores: Int = 0,
    val totalSeguidos: Int = 0,
    val totalCarpetas: Int = 0,
    val updatedAt: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()
) {
    constructor() : this("")
}