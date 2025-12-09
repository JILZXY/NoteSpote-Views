package com.example.notespote.viewModel.states

import com.example.notespote.domain.model.Apunte
import com.example.notespote.domain.model.Carpeta
import com.example.notespote.domain.model.Materia

data class HomeUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val userProfilePhoto: String? = null,
    val recentFolders: List<Carpeta> = emptyList(),
    val recentNotes: List<Apunte> = emptyList(),
    val hasContent: Boolean = false,
    val allNotes: List<Apunte> = emptyList(),
    val favoriteNotes: List<Apunte> = emptyList(),
    val deletedNotes: List<Apunte> = emptyList(),
    val materias: Map<String, Materia> = emptyMap()
) {
    fun getMateriaName(idMateria: String?): String {
        return idMateria?.let { materias[it]?.nombreMateria } ?: "Sin materia"
    }
}
