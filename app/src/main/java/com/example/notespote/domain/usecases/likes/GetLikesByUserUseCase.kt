// domain/usecases/likes/GetLikesByUserUseCase.kt
package com.example.notespote.domain.usecases.likes

import com.example.notespote.domain.model.Like
import com.example.notespote.domain.repository.LikeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLikesByUserUseCase @Inject constructor(
    private val likeRepository: LikeRepository
) {
    operator fun invoke(userId: String): Flow<Result<List<Like>>> {
        return likeRepository.getLikesByUser(userId)
    }
}