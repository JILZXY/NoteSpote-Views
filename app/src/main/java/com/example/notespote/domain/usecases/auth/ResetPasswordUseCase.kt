package com.example.notespote.domain.usecases.auth

import com.example.notespote.domain.repository.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (email.isBlank()) {
            return Result.failure(Exception("El email no puede estar vacío"))
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("El email no es válido"))
        }

        return authRepository.resetPassword(email)
    }
}