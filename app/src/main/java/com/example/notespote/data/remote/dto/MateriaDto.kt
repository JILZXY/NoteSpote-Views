package com.example.notespote.data.remote.dto

data class MateriaDto(
    val id: String = "",
    val nombreMateria: String = "",
    val categoria: String? = null,
    val descripcion: String? = null,
    val icono: String? = null,
    val isDeleted: Boolean = false
) {
    constructor() : this("")
}