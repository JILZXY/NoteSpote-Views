package com.example.notespote.data.remote.dto

import com.google.firebase.Timestamp

data class SyncMetadataDto(
    val idUsuario: String = "",
    val ultimaSincronizacion: Timestamp = Timestamp.now(),
    val dispositivoId: String = "",
    val versionApp: String = "",
    val plataforma: String = "Android"
) {
    constructor() : this("")
}