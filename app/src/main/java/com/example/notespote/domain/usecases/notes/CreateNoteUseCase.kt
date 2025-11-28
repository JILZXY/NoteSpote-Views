package com.example.notespote.domain.usecases.notes

import com.example.notespote.domain.model.SesionUsuario
import com.example.notespote.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<SesionUsuario> {
        if (email.isBlank()) {
            return Result.failure(Exception("El email no puede estar vacío"))
        }
        if (password.isBlank()) {
            return Result.failure(Exception("La contraseña no puede estar vacía"))
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("El email no es válido"))
        }

        return authRepository.login(email, password)
    }
}