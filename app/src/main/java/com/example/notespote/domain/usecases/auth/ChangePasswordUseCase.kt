package com.example.notespote.domain.usecases.auth

import com.example.notespote.domain.repository.AuthRepository
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        passwordActual: String,
        passwordNuevo: String,
        confirmarPassword: String
    ): Result<Unit> {
        if (passwordActual.isBlank()) {
            return Result.failure(Exception("La contraseña actual no puede estar vacía"))
        }
        if (passwordNuevo.isBlank()) {
            return Result.failure(Exception("La contraseña nueva no puede estar vacía"))
        }
        if (passwordNuevo.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        }
        if (passwordNuevo != confirmarPassword) {
            return Result.failure(Exception("Las contraseñas no coinciden"))
        }
        if (passwordActual == passwordNuevo) {
            return Result.failure(Exception("La nueva contraseña debe ser diferente a la actual"))
        }

        return authRepository.cambiarPassword(passwordActual, passwordNuevo)
    }
}