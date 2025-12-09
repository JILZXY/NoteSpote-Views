package com.example.notespote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notespote.domain.model.Apunte
import com.example.notespote.domain.repository.ApunteRepository
import com.example.notespote.domain.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommunityUiState(
    val apuntes: List<Apunte> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val apunteRepository: ApunteRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    init {
        loadPublicApuntes()
    }

    fun loadPublicApuntes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            apunteRepository.getPublicApuntes(50).collect { result ->
                result.onSuccess { apuntes ->
                    _uiState.value = _uiState.value.copy(
                        apuntes = apuntes,
                        isLoading = false
                    )
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        // Filtrar apuntes localmente por ahora
        filterApuntes(query)
    }

    private fun filterApuntes(query: String) {
        if (query.isBlank()) {
            loadPublicApuntes()
            return
        }

        viewModelScope.launch {
            apunteRepository.getPublicApuntes(50).collect { result ->
                result.onSuccess { apuntes ->
                    val filtered = apuntes.filter { apunte ->
                        apunte.titulo.contains(query, ignoreCase = true) ||
                        apunte.contenido?.contains(query, ignoreCase = true) == true
                    }
                    _uiState.value = _uiState.value.copy(
                        apuntes = filtered,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun refresh() {
        loadPublicApuntes()
    }
}
