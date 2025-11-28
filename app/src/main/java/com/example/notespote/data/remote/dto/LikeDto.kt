package com.example.notespote.data.remote.dto

import com.google.firebase.Timestamp

data class LikeDto(
    val id: String = "",
    val idUsuario: String = "",
    val idApunte: String = "",
    val fechaLike: Timestamp = Timestamp.now(),
    val isDeleted: Boolean = false
) {
    constructor() : this("")
}