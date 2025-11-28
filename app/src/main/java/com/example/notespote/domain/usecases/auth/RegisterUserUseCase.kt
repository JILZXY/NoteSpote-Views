package com.example.notespote.domain.usecases.auth

import com.example.notespote.domain.model.SesionUsuario
import com.example.notespote.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        nombreUsuario: String,
        nombre: String?,
        apellido: String?
    ): Result<SesionUsuario> {
        // Validaciones
        if (email.isBlank()) {
            return Result.failure(Exception("El email no puede estar vacío"))
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("El email no es válido"))
        }
        if (password.isBlank()) {
            return Result.failure(Exception("La contraseña no puede estar vacía"))
        }
        if (password.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        }
        if (nombreUsuario.isBlank()) {
            return Result.failure(Exception("El nombre de usuario no puede estar vacío"))
        }
        if (nombreUsuario.length < 3) {
            return Result.failure(Exception("El nombre de usuario debe tener al menos 3 caracteres"))
        }

        return authRepository.register(email, password, nombreUsuario, nombre, apellido)
    }
}