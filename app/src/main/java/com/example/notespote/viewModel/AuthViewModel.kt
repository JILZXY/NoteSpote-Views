// viewModel/AuthViewModel.kt
package com.example.notespote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notespote.domain.model.SesionUsuario
import com.example.notespote.domain.usecases.auth.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<SesionUsuario?>(null)
    val currentUser: StateFlow<SesionUsuario?> = _currentUser.asStateFlow()

    init {
        observeCurrentUser()
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { result ->
                result.onSuccess { usuario ->
                    if (usuario != null) {
                        _currentUser.value = SesionUsuario(
                            usuario = usuario,
                            estaAutenticado = true
                        )
                    } else {
                        _currentUser.value = null
                    }
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            val result = loginUseCase(email, password)

            _uiState.value = if (result.isSuccess) {
                AuthUiState.Success(result.getOrNull()!!)
            } else {
                AuthUiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun register(
        email: String,
        password: String,
        nombreUsuario: String,
        nombre: String?,
        apellido: String?
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            val result = registerUseCase(email, password, nombreUsuario, nombre, apellido)

            _uiState.value = if (result.isSuccess) {
                AuthUiState.Success(result.getOrNull()!!)
            } else {
                AuthUiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            val result = logoutUseCase()

            _uiState.value = if (result.isSuccess) {
                _currentUser.value = null
                AuthUiState.LoggedOut
            } else {
                AuthUiState.Error(result.exceptionOrNull()?.message ?: "Error al cerrar sesión")
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            val result = resetPasswordUseCase(email)

            _uiState.value = if (result.isSuccess) {
                AuthUiState.PasswordResetSent
            } else {
                AuthUiState.Error(result.exceptionOrNull()?.message ?: "Error al enviar correo")
            }
        }
    }

    fun changePassword(
        passwordActual: String,
        passwordNuevo: String,
        confirmarPassword: String
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            val result = changePasswordUseCase(passwordActual, passwordNuevo, confirmarPassword)

            _uiState.value = if (result.isSuccess) {
                AuthUiState.PasswordChanged
            } else {
                AuthUiState.Error(result.exceptionOrNull()?.message ?: "Error al cambiar contraseña")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = AuthUiState.Idle
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val sesion: SesionUsuario) : AuthUiState()
    object LoggedOut : AuthUiState()
    object PasswordResetSent : AuthUiState()
    object PasswordChanged : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}