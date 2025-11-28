// domain/usecases/likes/ToggleLikeUseCase.kt
package com.example.notespote.domain.usecases.likes

import com.example.notespote.domain.repository.AuthRepository
import com.example.notespote.domain.repository.LikeRepository
import javax.inject.Inject

class ToggleLikeUseCase @Inject constructor(
    private val likeRepository: LikeRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(apunteId: String): Result<Unit> {
        val userId = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("Usuario no autenticado"))

        return likeRepository.toggleLike(userId, apunteId)
    }
}