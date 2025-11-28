// domain/usecases/likes/HasLikedUseCase.kt
package com.example.notespote.domain.usecases.likes

import com.example.notespote.domain.repository.AuthRepository
import com.example.notespote.domain.repository.LikeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HasLikedUseCase @Inject constructor(
    private val likeRepository: LikeRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(apunteId: String): Flow<Result<Boolean>> {
        return flow {
            val userId = authRepository.getCurrentUserId()
            if (userId != null) {
                likeRepository.hasLiked(userId, apunteId).collect { result ->
                    emit(result)
                }
            } else {
                emit(Result.success(false))
            }
        }
    }
}