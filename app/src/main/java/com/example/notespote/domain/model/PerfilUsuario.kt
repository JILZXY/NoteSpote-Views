package com.example.notespote.domain.model

data class PerfilUsuario(
    val usuario: com.example.notespote.domain.model.Usuario,
    val apuntesPublicos: List<com.example.notespote.domain.model.Apunte> = emptyList(),
    val siguiendo: Boolean = false,
    val esPropio: Boolean = false
)