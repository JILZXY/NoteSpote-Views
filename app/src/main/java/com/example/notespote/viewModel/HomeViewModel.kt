// viewModel/HomeViewModel.kt
package com.example.notespote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notespote.domain.usecases.auth.GetCurrentUserUseCase
import com.example.notespote.domain.usecases.folders.CreateCarpetaUseCase
import com.example.notespote.domain.usecases.folders.GetCarpetasRaizUseCase
import com.example.notespote.domain.usecases.notes.GetMyApunteUseCase
import com.example.notespote.viewModel.states.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getMyApunteUseCase: GetMyApunteUseCase,
    private val getCarpetasRaizUseCase: GetCarpetasRaizUseCase,
    private val createCarpetaUseCase: CreateCarpetaUseCase,
    private val syncAllUseCase: com.example.notespote.domain.usecases.sync.SyncAllUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        syncDataFromFirebase()
        loadHomeData()
        observeCarpetas()
    }

    private fun syncDataFromFirebase() {
        viewModelScope.launch {
            try {
                android.util.Log.d("HomeViewModel", "Iniciando sincronización desde Firebase...")
                syncAllUseCase().onSuccess {
                    android.util.Log.d("HomeViewModel", "Sincronización completada exitosamente")
                }.onFailure { error ->
                    android.util.Log.e("HomeViewModel", "Error en sincronización: ${error.message}", error)
                }
            } catch (e: Exception) {
                android.util.Log.e("HomeViewModel", "Excepción durante sincronización", e)
            }
        }
    }

    private fun observeCarpetas() {
        viewModelScope.launch {
            // Primero obtenemos el usuario
            getCurrentUserUseCase().collect { userResult ->
                userResult.onSuccess { usuario ->
                    if (usuario != null && usuario.id != currentUserId) {
                        currentUserId = usuario.id
                        // Actualizar foto y nombre si cambian
                        _uiState.value = _uiState.value.copy(
                            userName = usuario.nombre ?: usuario.nombreUsuario,
                            userProfilePhoto = usuario.fotoPerfil
                        )
                        // Ahora observamos las carpetas continuamente
                        getCarpetasRaizUseCase(usuario.id).collect { carpetasResult ->
                            carpetasResult.onSuccess { carpetas ->
                                android.util.Log.d("HomeViewModel", "Carpetas actualizadas: ${carpetas.size} items")
                                _uiState.value = _uiState.value.copy(
                                    recentFolders = carpetas,
                                    isLoading = false
                                )
                                updateHasContent()
                            }
                        }
                    } else if (usuario != null) {
                        // Actualizar solo foto y nombre si cambian
                        _uiState.value = _uiState.value.copy(
                            userName = usuario.nombre ?: usuario.nombreUsuario,
                            userProfilePhoto = usuario.fotoPerfil
                        )
                    }
                }
            }
        }
    }

    fun loadHomeData() {
        viewModelScope.launch {
            android.util.Log.d("HomeViewModel", "loadHomeData started")
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Primero obtenemos el usuario actual
            val userResult = getCurrentUserUseCase().firstOrNull()
            android.util.Log.d("HomeViewModel", "userResult: $userResult")

            userResult?.onSuccess { usuario ->
                android.util.Log.d("HomeViewModel", "Usuario obtenido: ${usuario?.id}")
                if (usuario != null) {
                    // Tenemos un usuario, cargamos sus datos
                    val userId = usuario.id
                    val userName = if (!usuario.nombre.isNullOrBlank() && !usuario.apellido.isNullOrBlank()) {
                        "${usuario.nombre} ${usuario.apellido}"
                    } else if (!usuario.nombre.isNullOrBlank()) {
                        usuario.nombre
                    } else {
                        usuario.nombreUsuario
                    }
                    val userPhoto = usuario.fotoPerfil

                    // Actualizar nombre de usuario y foto
                    _uiState.value = _uiState.value.copy(
                        userName = userName,
                        userProfilePhoto = userPhoto
                    )

                    // Cargar carpetas
                    android.util.Log.d("HomeViewModel", "Loading carpetas for userId: $userId")
                    val carpetasResult = getCarpetasRaizUseCase(userId).firstOrNull()
                    android.util.Log.d("HomeViewModel", "carpetasResult: $carpetasResult")
                    carpetasResult?.onSuccess { carpetas ->
                        android.util.Log.d("HomeViewModel", "Carpetas loaded: ${carpetas.size} items")
                        carpetas.forEach { carpeta ->
                            android.util.Log.d("HomeViewModel", "  - ${carpeta.nombreCarpeta} (${carpeta.colorCarpeta})")
                        }
                        _uiState.value = _uiState.value.copy(
                            recentFolders = carpetas, // Guardar TODAS las carpetas, no solo 5
                            userName = userName
                        )
                        android.util.Log.d("HomeViewModel", "UI State updated with ${_uiState.value.recentFolders.size} folders")
                    }

                    // Cargar apuntes
                    getMyApunteUseCase(userId).collect { apuntesResult ->
                        apuntesResult.onSuccess { apuntes ->
                            android.util.Log.d("HomeViewModel", "Apuntes loaded: ${apuntes.size} items")
                            _uiState.value = _uiState.value.copy(
                                recentNotes = apuntes.take(5)
                            )
                            updateHasContent()
                        }.onFailure { error ->
                            android.util.Log.e("HomeViewModel", "Error loading apuntes: ${error.message}")
                        }
                    }
                } else {
                    android.util.Log.w("HomeViewModel", "No current user found")
                    // No hay usuario actual, estado por defecto
                    _uiState.value = HomeUiState(
                        isLoading = false,
                        hasContent = false,
                        userName = "Usuario"
                    )
                }
            }?.onFailure { error ->
                android.util.Log.e("HomeViewModel", "Error loading user: ${error.message}", error)
                // Error al obtener usuario
                _uiState.value = HomeUiState(
                    isLoading = false,
                    hasContent = false,
                    userName = "Usuario"
                )
            } ?: run {
                android.util.Log.w("HomeViewModel", "userResult is null")
                // userResult es null
                _uiState.value = HomeUiState(
                    isLoading = false,
                    hasContent = false,
                    userName = "Usuario"
                )
            }
        }
    }

    private fun updateHasContent() {
        val currentState = _uiState.value
        val hasContent = currentState.recentFolders.isNotEmpty() || currentState.recentNotes.isNotEmpty()
        android.util.Log.d("HomeViewModel", "updateHasContent: folders=${currentState.recentFolders.size}, notes=${currentState.recentNotes.size}, hasContent=$hasContent")
        _uiState.value = currentState.copy(
            isLoading = false,
            hasContent = hasContent
        )
    }

    fun refresh() {
        loadHomeData()
    }

    fun createFolder(nombre: String, color: String) {
        viewModelScope.launch {
            android.util.Log.d("HomeViewModel", "createFolder called with nombre=$nombre, color=$color")

            val result = createCarpetaUseCase(
                nombreCarpeta = nombre,
                colorCarpeta = color,
                descripcion = null,
                idCarpetaPadre = null,
                idMateria = null
            )

            result.onSuccess { carpetaId ->
                android.util.Log.d("HomeViewModel", "Carpeta created successfully with ID: $carpetaId")
                // Dar tiempo para que Room emita el nuevo valor en el Flow
                kotlinx.coroutines.delay(100)
                // Recargar los datos después de crear la carpeta
                refresh()
            }.onFailure { error ->
                android.util.Log.e("HomeViewModel", "Error creating carpeta: ${error.message}", error)
                // Manejar el error (podrías agregar un estado de error al UiState)
            }
        }
    }
}
