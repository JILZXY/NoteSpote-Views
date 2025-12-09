package com.example.notespote.domain.usecases.auth

import com.example.notespote.domain.model.Usuario
import com.example.notespote.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) {
    suspend operator fun invoke(
        nombre: String?,
        apellido: String?,
        nombreUsuario: String,
        biografia: String?
    ): Result<Unit> {
        if (nombreUsuario.isBlank()) {
            return Result.failure(Exception("El nombre de usuario no puede estar vacío"))
        }

        if (nombreUsuario.length < 3) {
            return Result.failure(Exception("El nombre de usuario debe tener al menos 3 caracteres"))
        }

        // Obtener el usuario actual
        val currentUserResult = usuarioRepository.getUsuarioActual().firstOrNull()
        val currentUser = currentUserResult?.getOrNull()

        if (currentUser == null) {
            return Result.failure(Exception("No se encontró el usuario actual"))
        }

        // Actualizar el usuario con los nuevos datos
        val updatedUser = currentUser.copy(
            nombre = nombre?.takeIf { it.isNotBlank() },
            apellido = apellido?.takeIf { it.isNotBlank() },
            nombreUsuario = nombreUsuario,
            biografia = biografia?.takeIf { it.isNotBlank() }
        )

        return usuarioRepository.updateUsuario(updatedUser)
    }
}
