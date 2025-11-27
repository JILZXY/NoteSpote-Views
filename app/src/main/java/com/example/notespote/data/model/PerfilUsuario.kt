package com.example.notespote.data.model

data class PerfilUsuario(
    val usuario: Usuario,
    val apuntesPublicos: List<Apunte> = emptyList(),
    val siguiendo: Boolean = false,
    val esPropio: Boolean = false
)