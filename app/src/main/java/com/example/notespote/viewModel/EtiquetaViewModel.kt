// viewModel/EtiquetaViewModel.kt
package com.example.notespote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notespote.domain.model.Etiqueta
import com.example.notespote.domain.usecases.etiquetas.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EtiquetaViewModel @Inject constructor(
    private val getEtiquetasByApunteUseCase: GetEtiquetasByApunteUseCase,
    private val getEtiquetaByIdUseCase: GetEtiquetaByIdUseCase,
    private val searchEtiquetasUseCase: SearchEtiquetasUseCase,
    private val agregarEtiquetaAApunteUseCase: AgregarEtiquetaAApunteUseCase,
    private val removerEtiquetaDeApunteUseCase: RemoverEtiquetaDeApunteUseCase,
    private val getTopEtiquetasUseCase: GetTopEtiquetasUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<EtiquetaUiState>(EtiquetaUiState.Idle)
    val uiState: StateFlow<EtiquetaUiState> = _uiState.asStateFlow()

    private val _etiquetas = MutableStateFlow<List<Etiqueta>>(emptyList())
    val etiquetas: StateFlow<List<Etiqueta>> = _etiquetas.asStateFlow()

    private val _etiqueta = MutableStateFlow<Etiqueta?>(null)
    val etiqueta: StateFlow<Etiqueta?> = _etiqueta.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Etiqueta>>(emptyList())
    val searchResults: StateFlow<List<Etiqueta>> = _searchResults.asStateFlow()

    fun getEtiquetaById(etiquetaId: String) {
        viewModelScope.launch {
            _uiState.value = EtiquetaUiState.Loading

            getEtiquetaByIdUseCase(etiquetaId).collect { result ->
                result.onSuccess { etiqueta ->
                    _etiqueta.value = etiqueta
                    _uiState.value = EtiquetaUiState.SuccessEtiqueta
                }.onFailure { error ->
                    _uiState.value = EtiquetaUiState.Error(error.message ?: "Error al cargar etiqueta")
                }
            }
        }
    }

    fun loadEtiquetasByApunte(apunteId: String) {
        viewModelScope.launch {
            _uiState.value = EtiquetaUiState.Loading

            getEtiquetasByApunteUseCase(apunteId).collect { result ->
                result.onSuccess { etiquetasList ->
                    _etiquetas.value = etiquetasList
                    _uiState.value = EtiquetaUiState.Success
                }.onFailure { error ->
                    _uiState.value = EtiquetaUiState.Error(error.message ?: "Error al cargar etiquetas")
                }
            }
        }
    }

    fun loadTopEtiquetas(limit: Int = 20) {
        viewModelScope.launch {
            _uiState.value = EtiquetaUiState.Loading

            getTopEtiquetasUseCase(limit).collect { result ->
                result.onSuccess { etiquetasList ->
                    _etiquetas.value = etiquetasList
                    _uiState.value = EtiquetaUiState.Success
                }.onFailure { error ->
                    _uiState.value = EtiquetaUiState.Error(error.message ?: "Error al cargar etiquetas")
                }
            }
        }
    }

    fun searchEtiquetas(query: String, limit: Int = 10) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _searchResults.value = emptyList()
                return@launch
            }

            searchEtiquetasUseCase(query, limit).collect { result ->
                result.onSuccess { etiquetasList ->
                    _searchResults.value = etiquetasList
                }.onFailure { error ->
                    _uiState.value = EtiquetaUiState.Error(error.message ?: "Error al buscar etiquetas")
                }
            }
        }
    }

    fun agregarEtiqueta(apunteId: String, nombreEtiqueta: String) {
        viewModelScope.launch {
            _uiState.value = EtiquetaUiState.Loading

            val result = agregarEtiquetaAApunteUseCase(apunteId, nombreEtiqueta)

            _uiState.value = if (result.isSuccess) {
                EtiquetaUiState.Added
            } else {
                EtiquetaUiState.Error(result.exceptionOrNull()?.message ?: "Error al agregar etiqueta")
            }
        }
    }

    fun removerEtiqueta(apunteId: String, etiquetaId: String) {
        viewModelScope.launch {
            _uiState.value = EtiquetaUiState.Loading

            val result = removerEtiquetaDeApunteUseCase(apunteId, etiquetaId)

            _uiState.value = if (result.isSuccess) {
                EtiquetaUiState.Removed
            } else {
                EtiquetaUiState.Error(result.exceptionOrNull()?.message ?: "Error al remover etiqueta")
            }
        }
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }

    fun resetUiState() {
        _uiState.value = EtiquetaUiState.Idle
    }
}

sealed class EtiquetaUiState {
    object Idle : EtiquetaUiState()
    object Loading : EtiquetaUiState()
    object Success : EtiquetaUiState()
    object SuccessEtiqueta : EtiquetaUiState()
    object Added : EtiquetaUiState()
    object Removed : EtiquetaUiState()
    data class Error(val message: String) : EtiquetaUiState()
}