package com.example.notespote.domain.model

data class Note(
    val title: String,
    val subject: String,
    val description: String,
    val isPublic: Boolean
)

data class Folder(
    val title: String,
    val color: String,
    val isPublic: Boolean
)
