package com.example.notespote.domain.usecases.auth

import com.example.notespote.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class UpdateEmailUseCase @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) {
    suspend operator fun invoke(newEmail: String): Result<Unit> {
        if (newEmail.isBlank()) {
            return Result.failure(Exception("El correo electrónico no puede estar vacío"))
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            return Result.failure(Exception("El correo electrónico no es válido"))
        }

        // Obtener el usuario actual
        val currentUserResult = usuarioRepository.getUsuarioActual().firstOrNull()
        val currentUser = currentUserResult?.getOrNull()

        if (currentUser == null) {
            return Result.failure(Exception("No se encontró el usuario actual"))
        }

        // Actualizar el email
        val updatedUser = currentUser.copy(email = newEmail)
        return usuarioRepository.updateUsuario(updatedUser)
    }
}
