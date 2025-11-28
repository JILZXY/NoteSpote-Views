package com.example.notespote.domain.usecases.postits

import com.example.notespote.domain.repository.PostitRepository
import javax.inject.Inject

class DeletePostitUseCase @Inject constructor(
    private val postitRepository: PostitRepository
) {
    suspend operator fun invoke(postitId: String): Result<Unit> {
        return postitRepository.deletePostit(postitId)
    }
}