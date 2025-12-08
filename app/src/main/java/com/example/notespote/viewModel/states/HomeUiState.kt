package com.example.notespote.viewModel.states

import com.example.notespote.domain.model.Apunte
import com.example.notespote.domain.model.Carpeta

data class HomeUiState(
    val isLoading: Boolean = true,
    val hasContent: Boolean = false,
    val recentFolders: List<Carpeta> = emptyList(),
    val recentNotes: List<Apunte> = emptyList(),
    val userName: String = "",
    val userProfilePhoto: String? = null
)
