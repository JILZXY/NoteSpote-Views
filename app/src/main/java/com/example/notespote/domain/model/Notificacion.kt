package com.example.notespote.domain.model

data class Notificacion(
    val id: String = "",
    val idUsuario: String = "",
    val tipo: com.example.notespote.domain.model.TipoNotificacion,
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