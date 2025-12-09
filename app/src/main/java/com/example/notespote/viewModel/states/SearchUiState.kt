package com.example.notespote.viewModel.states

import com.example.notespote.domain.model.Apunte
import com.example.notespote.domain.model.Carpeta

data class SearchUiState(
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val carpetas: List<Carpeta> = emptyList(),
    val apuntes: List<Apunte> = emptyList(),
    val selectedFilters: Set<SearchFilter> = emptySet(),
    val error: String? = null
)

enum class SearchFilter {
    CARPETAS,
    APUNTES,
    SOLO_IMAGENES,
    SOLO_POSTITS
}
