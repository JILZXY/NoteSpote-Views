package com.example.notespote.domain.model

data class Materia(
    val id: String = "",
    val nombreMateria: String = "",
    val categoria: String? = null,
    val descripcion: String? = null,
    val icono: String? = null
)