package com.example.notespote.viewModel.states

import android.net.Uri

data class EditProfileUiState(
    val nombre: String = "",
    val apellido: String = "",
    val nombreUsuario: String = "",
    val email: String = "",
    val biografia: String = "",
    val fotoPerfilUrl: String? = null,
    val fotoPerfilUri: Uri? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val showPasswordDialog: Boolean = false
)

data class ChangePasswordUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)
