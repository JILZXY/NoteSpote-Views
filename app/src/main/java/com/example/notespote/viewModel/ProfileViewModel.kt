package com.example.notespote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notespote.data.local.entities.TipoVisibilidad
import com.example.notespote.domain.model.Apunte
import com.example.notespote.domain.model.Carpeta
import com.example.notespote.domain.model.Usuario
import com.example.notespote.domain.usecases.auth.GetCurrentUserUseCase
import com.example.notespote.domain.usecases.folders.GetCarpetasRaizUseCase
import com.example.notespote.domain.usecases.notes.GetApuntesByUserUseCase
import com.example.notespote.domain.usecases.seguimiento.GetSeguidoresCountUseCase
import com.example.notespote.domain.usecases.seguimiento.GetSigueCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: Usuario? = null,
    val publicNotes: List<Apunte> = emptyList(),
    val privateNotes: List<Apunte> = emptyList(),
    val publicFolders: List<Carpeta> = emptyList(),
    val privateFolders: List<Carpeta> = emptyList(),
    val followerCount: Long = 0,
    val followingCount: Long = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getApuntesByUserUseCase: GetApuntesByUserUseCase,
    private val getCarpetasRaizUseCase: GetCarpetasRaizUseCase,
    private val getSeguidoresCountUseCase: GetSeguidoresCountUseCase,
    private val getSigueCountUseCase: GetSigueCountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        loadProfileData()
        observeProfileChanges()
    }

    private fun observeProfileChanges() {
        viewModelScope.launch {
            // Observar cambios en el usuario actual
            getCurrentUserUseCase().collect { userResult ->
                userResult.onSuccess { usuario ->
                    if (usuario != null && usuario.id != currentUserId) {
                        currentUserId = usuario.id
                        _uiState.value = _uiState.value.copy(user = usuario)
                        loadUserData(usuario.id)
                    } else if (usuario != null) {
                        _uiState.value = _uiState.value.copy(user = usuario)
                    }
                }
            }
        }
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val userResult = getCurrentUserUseCase().firstOrNull()
                val user = userResult?.getOrNull()

                if (user != null) {
                    currentUserId = user.id
                    _uiState.value = _uiState.value.copy(user = user)
                    loadUserData(user.id)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No se encontró el usuario"
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("ProfileViewModel", "Error loading profile data", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    private suspend fun loadUserData(userId: String) {
        viewModelScope.launch {
            // Cargar apuntes
            getApuntesByUserUseCase(userId).collect { notesResult ->
                notesResult.onSuccess { notes ->
                    val publicNotes = notes.filter { it.tipoVisibilidad == TipoVisibilidad.PUBLICO }
                    val privateNotes = notes.filter { it.tipoVisibilidad == TipoVisibilidad.PRIVADO }

                    _uiState.value = _uiState.value.copy(
                        publicNotes = publicNotes,
                        privateNotes = privateNotes
                    )
                }
            }
        }

        viewModelScope.launch {
            // Cargar carpetas
            getCarpetasRaizUseCase(userId).collect { foldersResult ->
                foldersResult.onSuccess { folders ->
                    // Por ahora todas las carpetas son públicas, pero puedes agregar lógica aquí
                    _uiState.value = _uiState.value.copy(
                        publicFolders = folders,
                        privateFolders = emptyList(),
                        isLoading = false
                    )
                }
            }
        }

        // Cargar contadores
        viewModelScope.launch {
            val followersResult = getSeguidoresCountUseCase(userId)
            val followingResult = getSigueCountUseCase(userId)

            _uiState.value = _uiState.value.copy(
                followerCount = followersResult.getOrDefault(0).toLong(),
                followingCount = followingResult.getOrDefault(0).toLong()
            )
        }
    }

    fun refresh() {
        loadProfileData()
    }
}
