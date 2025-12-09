package com.example.notespote.domain.usecases.auth

import android.net.Uri
import com.example.notespote.domain.repository.AuthRepository
import com.example.notespote.domain.repository.UsuarioRepository
import javax.inject.Inject

class UpdateProfilePhotoUseCase @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(photoUri: Uri): Result<String> {
        // Forzar la actualización del token antes de la operación
        authRepository.forceTokenRefresh().onFailure {
            return Result.failure(it)
        }

        val userId = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("Usuario no autenticado"))

        return usuarioRepository.updateFotoPerfil(userId, photoUri)
    }
}
