// domain/usecases/likes/GetLikesByApunteUseCase.kt
package com.example.notespote.domain.usecases.likes

import com.example.notespote.domain.model.Like
import com.example.notespote.domain.repository.LikeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLikesByApunteUseCase @Inject constructor(
    private val likeRepository: LikeRepository
) {
    operator fun invoke(apunteId: String): Flow<Result<List<Like>>> {
        return likeRepository.getLikesByApunte(apunteId)
    }
}