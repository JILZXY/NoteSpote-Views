package com.example.notespote.domain.model

import java.util.UUID

/**
 * Representa un bloque de contenido dentro de una nota.
 * Cada bloque puede ser texto, imagen, PDF o un Post-it.
 */
sealed interface NoteBlock {
    val id: String
    val order: Int

    data class TextBlock(
        override val id: String = UUID.randomUUID().toString(),
        override val order: Int = 0,
        val content: String = ""
    ) : NoteBlock

    data class ImageBlock(
        override val id: String = UUID.randomUUID().toString(),
        override val order: Int = 0,
        val rutaLocal: String,
        val nombreArchivo: String,
        val description: String = ""
    ) : NoteBlock

    data class PdfBlock(
        override val id: String = UUID.randomUUID().toString(),
        override val order: Int = 0,
        val rutaLocal: String,
        val nombreArchivo: String,
        val tamanoKb: Int = 0
    ) : NoteBlock

    data class PostipBlock(
        override val id: String = UUID.randomUUID().toString(),
        override val order: Int = 0,
        val content: String = "",
        val colorHex: String = "#FFEB3B" // Amarillo pastel por defecto
    ) : NoteBlock
}

/**
 * Colores predefinidos para los Post-its
 */
object PostipColors {
    const val YELLOW = "#FFEB3B"      // Amarillo
    const val PINK = "#F48FB1"        // Rosa
    const val BLUE = "#81D4FA"        // Azul claro
    const val GREEN = "#A5D6A7"       // Verde claro
    const val ORANGE = "#FFCC80"      // Naranja
    const val PURPLE = "#CE93D8"      // Púrpura

    val all = listOf(YELLOW, PINK, BLUE, GREEN, ORANGE, PURPLE)
}
