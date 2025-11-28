// domain/usecases/likes/GetLikesCountUseCase.kt
package com.example.notespote.domain.usecases.likes

import com.example.notespote.domain.repository.LikeRepository
import javax.inject.Inject

class GetLikesCountUseCase @Inject constructor(
    private val likeRepository: LikeRepository
) {
    suspend operator fun invoke(apunteId: String): Result<Int> {
        return likeRepository.getLikesCount(apunteId)
    }
}