package com.example.notespote.domain.model

/**
 * Represents a single folder in the application.
 * This data class will be used as a Room entity.
 */
data class Folder(
    val title: String,
    val colorHex: String, // Storing color as a hex string
    val isPublic: Boolean
)
