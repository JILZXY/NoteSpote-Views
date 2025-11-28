// viewModel/SeguimientoViewModel.kt
package com.example.notespote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notespote.domain.model.Usuario
import com.example.notespote.domain.usecases.seguimiento.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeguimientoViewModel @Inject constructor(
    private val toggleSeguirUseCase: ToggleSeguirUseCase,
    private val isSiguiendoUseCase: IsSiguiendoUseCase,
    private val getSeguidosUseCase: GetSeguidosUseCase,
    private val getSeguidoresUseCase: GetSeguidoresUseCase,
    private val getSigueCountUseCase: GetSigueCountUseCase,
    private val getSeguidoresCountUseCase: GetSeguidoresCountUseCase,
    private val getMisSeguidosUseCase: GetMisSeguidosUseCase,
    private val getMisSeguidoresUseCase: GetMisSeguidoresUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SeguimientoUiState>(SeguimientoUiState.Idle)
    val uiState: StateFlow<SeguimientoUiState> = _uiState.asStateFlow()

    private val _seguidos = MutableStateFlow<List<Usuario>>(emptyList())
    val seguidos: StateFlow<List<Usuario>> = _seguidos.asStateFlow()

    private val _seguidores = MutableStateFlow<List<Usuario>>(emptyList())
    val seguidores: StateFlow<List<Usuario>> = _seguidores.asStateFlow()

    // Mapa de userId -> isSiguiendo
    private val _siguiendoMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val siguiendoMap: StateFlow<Map<String, Boolean>> = _siguiendoMap.asStateFlow()

    // Contadores
    private val _sigueCount = MutableStateFlow(0)
    val sigueCount: StateFlow<Int> = _sigueCount.asStateFlow()

    private val _seguidoresCount = MutableStateFlow(0)
    val seguidoresCount: StateFlow<Int> = _seguidoresCount.asStateFlow()

    fun checkIfSiguiendo(userId: String) {
        viewModelScope.launch {
            isSiguiendoUseCase(userId).collect { result ->
                result.onSuccess { isSiguiendo ->
                    _siguiendoMap.value = _siguiendoMap.value.toMutableMap().apply {
                        put(userId, isSiguiendo)
                    }
                }
            }
        }
    }

    fun loadSeguidos(userId: String) {
        viewModelScope.launch {
            _uiState.value = SeguimientoUiState.Loading

            getSeguidosUseCase(userId).collect { result ->
                result.onSuccess { usuarios ->
                    _seguidos.value = usuarios
                    _uiState.value = SeguimientoUiState.Success
                }.onFailure { error ->
                    _uiState.value = SeguimientoUiState.Error(error.message ?: "Error al cargar seguidos")
                }
            }
        }
    }

    fun loadSeguidores(userId: String) {
        viewModelScope.launch {
            _uiState.value = SeguimientoUiState.Loading

            getSeguidoresUseCase(userId).collect { result ->
                result.onSuccess { usuarios ->
                    _seguidores.value = usuarios
                    _uiState.value = SeguimientoUiState.Success
                }.onFailure { error ->
                    _uiState.value = SeguimientoUiState.Error(error.message ?: "Error al cargar seguidores")
                }
            }
        }
    }

    fun loadMisSeguidos() {
        viewModelScope.launch {
            _uiState.value = SeguimientoUiState.Loading

            getMisSeguidosUseCase().collect { result ->
                result.onSuccess { usuarios ->
                    _seguidos.value = usuarios
                    _uiState.value = SeguimientoUiState.Success
                }.onFailure { error ->
                    _uiState.value = SeguimientoUiState.Error(error.message ?: "Error al cargar seguidos")
                }
            }
        }
    }

    fun loadMisSeguidores() {
        viewModelScope.launch {
            _uiState.value = SeguimientoUiState.Loading

            getMisSeguidoresUseCase().collect { result ->
                result.onSuccess { usuarios ->
                    _seguidores.value = usuarios
                    _uiState.value = SeguimientoUiState.Success
                }.onFailure { error ->
                    _uiState.value = SeguimientoUiState.Error(error.message ?: "Error al cargar seguidores")
                }
            }
        }
    }

    fun loadCounts(userId: String) {
        viewModelScope.launch {
            // Cargar contadores
            val sigueResult = getSigueCountUseCase(userId)
            sigueResult.onSuccess { count ->
                _sigueCount.value = count
            }

            val seguidoresResult = getSeguidoresCountUseCase(userId)
            seguidoresResult.onSuccess { count ->
                _seguidoresCount.value = count
            }
        }
    }

    fun toggleSeguir(userId: String) {
        viewModelScope.launch {
            _uiState.value = SeguimientoUiState.Loading

            val result = toggleSeguirUseCase(userId)

            _uiState.value = if (result.isSuccess) {
                // Actualizar estado local
                val currentSiguiendo = _siguiendoMap.value[userId] ?: false
                _siguiendoMap.value = _siguiendoMap.value.toMutableMap().apply {
                    put(userId, !currentSiguiendo)
                }

                // Actualizar contador
                if (currentSiguiendo) {
                    _sigueCount.value = _sigueCount.value - 1
                } else {
                    _sigueCount.value = _sigueCount.value + 1
                }

                SeguimientoUiState.Success
            } else {
                SeguimientoUiState.Error(result.exceptionOrNull()?.message ?: "Error al procesar seguimiento")
            }
        }
    }

    fun isSiguiendo(userId: String): Boolean {
        return _siguiendoMap.value[userId] ?: false
    }

    fun resetUiState() {
        _uiState.value = SeguimientoUiState.Idle
    }
}

sealed class SeguimientoUiState {
    object Idle : SeguimientoUiState()
    object Loading : SeguimientoUiState()
    object Success : SeguimientoUiState()
    data class Error(val message: String) : SeguimientoUiState()
}