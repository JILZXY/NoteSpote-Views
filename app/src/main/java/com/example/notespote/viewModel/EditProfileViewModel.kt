package com.example.notespote.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notespote.domain.usecases.auth.ChangePasswordUseCase
import com.example.notespote.domain.usecases.auth.GetCurrentUserUseCase
import com.example.notespote.domain.usecases.auth.UpdateEmailUseCase
import com.example.notespote.domain.usecases.auth.UpdateProfilePhotoUseCase
import com.example.notespote.domain.usecases.auth.UpdateUserProfileUseCase
import com.example.notespote.viewModel.states.ChangePasswordUiState
import com.example.notespote.viewModel.states.EditProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val updateProfilePhotoUseCase: UpdateProfilePhotoUseCase,
    private val updateEmailUseCase: UpdateEmailUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private val _passwordState = MutableStateFlow(ChangePasswordUiState())
    val passwordState: StateFlow<ChangePasswordUiState> = _passwordState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            getCurrentUserUseCase().firstOrNull()?.onSuccess { usuario ->
                if (usuario != null) {
                    _uiState.value = _uiState.value.copy(
                        nombre = usuario.nombre ?: "",
                        apellido = usuario.apellido ?: "",
                        nombreUsuario = usuario.nombreUsuario,
                        email = usuario.email,
                        biografia = usuario.biografia ?: "",
                        fotoPerfilUrl = usuario.fotoPerfil,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No se encontró el usuario"
                    )
                }
            }?.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Error al cargar el usuario"
                )
            }
        }
    }

    fun onNombreChange(nombre: String) {
        _uiState.value = _uiState.value.copy(nombre = nombre)
    }

    fun onApellidoChange(apellido: String) {
        _uiState.value = _uiState.value.copy(apellido = apellido)
    }

    fun onNombreUsuarioChange(nombreUsuario: String) {
        _uiState.value = _uiState.value.copy(nombreUsuario = nombreUsuario)
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onBiografiaChange(biografia: String) {
        _uiState.value = _uiState.value.copy(biografia = biografia)
    }

    fun onProfilePhotoSelected(uri: Uri) {
        _uiState.value = _uiState.value.copy(fotoPerfilUri = uri)
    }

    fun saveProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null, successMessage = null)

            // Primero, actualizar la foto de perfil si se seleccionó una nueva
            _uiState.value.fotoPerfilUri?.let { uri ->
                updateProfilePhotoUseCase(uri).onSuccess { newPhotoUrl ->
                    _uiState.value = _uiState.value.copy(
                        fotoPerfilUrl = newPhotoUrl,
                        fotoPerfilUri = null
                    )
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = "Error al actualizar la foto: ${error.message}"
                    )
                    return@launch
                }
            }

            // Actualizar el perfil del usuario
            val result = updateUserProfileUseCase(
                nombre = _uiState.value.nombre.takeIf { it.isNotBlank() },
                apellido = _uiState.value.apellido.takeIf { it.isNotBlank() },
                nombreUsuario = _uiState.value.nombreUsuario,
                biografia = _uiState.value.biografia.takeIf { it.isNotBlank() }
            )

            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    successMessage = "Perfil actualizado correctamente"
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = error.message ?: "Error al actualizar el perfil"
                )
            }
        }
    }

    fun updateEmail() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null, successMessage = null)

            val result = updateEmailUseCase(_uiState.value.email)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    successMessage = "Correo actualizado correctamente"
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = error.message ?: "Error al actualizar el correo"
                )
            }
        }
    }

    fun showPasswordDialog() {
        _uiState.value = _uiState.value.copy(showPasswordDialog = true)
    }

    fun hidePasswordDialog() {
        _uiState.value = _uiState.value.copy(showPasswordDialog = false)
        _passwordState.value = ChangePasswordUiState()
    }

    fun onCurrentPasswordChange(password: String) {
        _passwordState.value = _passwordState.value.copy(currentPassword = password)
    }

    fun onNewPasswordChange(password: String) {
        _passwordState.value = _passwordState.value.copy(newPassword = password)
    }

    fun onConfirmPasswordChange(password: String) {
        _passwordState.value = _passwordState.value.copy(confirmPassword = password)
    }

    fun changePassword() {
        viewModelScope.launch {
            _passwordState.value = _passwordState.value.copy(isLoading = true, error = null, successMessage = null)

            val result = changePasswordUseCase(
                passwordActual = _passwordState.value.currentPassword,
                passwordNuevo = _passwordState.value.newPassword,
                confirmarPassword = _passwordState.value.confirmPassword
            )

            result.onSuccess {
                _passwordState.value = _passwordState.value.copy(
                    isLoading = false,
                    successMessage = "Contraseña actualizada correctamente"
                )
                // Esperar un poco antes de cerrar el diálogo
                kotlinx.coroutines.delay(1500)
                hidePasswordDialog()
            }.onFailure { error ->
                _passwordState.value = _passwordState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Error al cambiar la contraseña"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    fun refresh() {
        loadCurrentUser()
    }
}
