package com.example.notespote.data.model

data class Notificacion(
    val id: String = "",
    val idUsuario: String = "",
    val tipo: TipoNotificacion,
    val titulo: String = "",
    val mensaje: String = "",
    val idReferencia: String? = null,
    val leida: Boolean = false,
    val fecha: Long = System.currentTimeMillis()
)

enum class TipoNotificacion {
    NUEVO_SEGUIDOR,
    LIKE_APUNTE,
    APUNTE_GUARDADO,
}