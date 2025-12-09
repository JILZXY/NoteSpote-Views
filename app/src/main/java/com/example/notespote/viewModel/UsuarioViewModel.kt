// viewModel/UsuarioViewModel.kt
package com.example.notespote.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notespote.domain.model.EstadisticasUsuario
import com.example.notespote.domain.model.PerfilUsuario
import com.example.notespote.domain.model.Usuario
import com.example.notespote.domain.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsuarioViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UsuarioUiState>(UsuarioUiState.Idle)
    val uiState: StateFlow<UsuarioUiState> = _uiState.asStateFlow()

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    private val _perfilUsuario = MutableStateFlow<PerfilUsuario?>(null)
    val perfilUsuario: StateFlow<PerfilUsuario?> = _perfilUsuario.asStateFlow()

    private val _estadisticas = MutableStateFlow<EstadisticasUsuario?>(null)
    val estadisticas: StateFlow<EstadisticasUsuario?> = _estadisticas.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Usuario>>(emptyList())
    val searchResults: StateFlow<List<Usuario>> = _searchResults.asStateFlow()

    fun loadUsuarioById(userId: String) {
        viewModelScope.launch {
            _uiState.value = UsuarioUiState.Loading

            usuarioRepository.getUsuarioById(userId).collect { result ->
                result.onSuccess { user ->
                    _usuario.value = user
                    _uiState.value = UsuarioUiState.Success
                }.onFailure { error ->
                    _uiState.value = UsuarioUiState.Error(error.message ?: "Error al cargar usuario")
                }
            }
        }
    }

    fun loadPerfilUsuario(userId: String) {
        viewModelScope.launch {
            _uiState.value = UsuarioUiState.Loading

            usuarioRepository.getPerfilUsuario(userId).collect { result ->
                result.onSuccess { perfil ->
                    _perfilUsuario.value = perfil
                    _uiState.value = UsuarioUiState.Success
                }.onFailure { error ->
                    _uiState.value = UsuarioUiState.Error(error.message ?: "Error al cargar perfil")
                }
            }
        }
    }

    fun loadEstadisticas(userId: String) {
        viewModelScope.launch {
            val result = usuarioRepository.getEstadisticas(userId)

            result.onSuccess { stats ->
                _estadisticas.value = stats
            }.onFailure { error ->
                _uiState.value = UsuarioUiState.Error(error.message ?: "Error al cargar estadísticas")
            }
        }
    }

    fun searchUsuarios(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _searchResults.value = emptyList()
                return@launch
            }

            _uiState.value = UsuarioUiState.Loading

            usuarioRepository.searchUsuarios(query).collect { result ->
                result.onSuccess { usuarios ->
                    _searchResults.value = usuarios
                    _uiState.value = UsuarioUiState.Success
                }.onFailure { error ->
                    _uiState.value = UsuarioUiState.Error(error.message ?: "Error al buscar usuarios")
                }
            }
        }
    }

    fun updateUsuario(usuario: Usuario) {
        viewModelScope.launch {
            _uiState.value = UsuarioUiState.Loading

            val result = usuarioRepository.updateUsuario(usuario)

            _uiState.value = if (result.isSuccess) {
                UsuarioUiState.Updated
            } else {
                UsuarioUiState.Error(result.exceptionOrNull()?.message ?: "Error al actualizar usuario")
            }
        }
    }

    fun updateFotoPerfil(userId: String, uri: Uri) {
        viewModelScope.launch {
            _uiState.value = UsuarioUiState.Loading

            val result = usuarioRepository.updateFotoPerfil(userId, uri)

            _uiState.value = if (result.isSuccess) {
                UsuarioUiState.FotoUpdated(result.getOrNull()!!)
            } else {
                UsuarioUiState.Error(result.exceptionOrNull()?.message ?: "Error al actualizar foto")
            }
        }
    }

    fun updateBiografia(userId: String, biografia: String) {
        viewModelScope.launch {
            _uiState.value = UsuarioUiState.Loading

            val result = usuarioRepository.updateBiografia(userId, biografia)

            _uiState.value = if (result.isSuccess) {
                UsuarioUiState.Updated
            } else {
                UsuarioUiState.Error(result.exceptionOrNull()?.message ?: "Error al actualizar biografía")
            }
        }
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }

    fun resetUiState() {
        _uiState.value = UsuarioUiState.Idle
    }
}

sealed class UsuarioUiState {
    object Idle : UsuarioUiState()
    object Loading : UsuarioUiState()
    object Success : UsuarioUiState()
    object Updated : UsuarioUiState()
    data class FotoUpdated(val url: String) : UsuarioUiState()
    data class Error(val message: String) : UsuarioUiState()
}