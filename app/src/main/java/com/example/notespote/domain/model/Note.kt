package com.example.notespote.domain.model

/**
 * Represents a single note in the application.
 * This data class will be used as a Room entity.
 */
data class Note(
    val title: String,
    val subject: String,
    val description: String,
    val isPublic: Boolean
)
