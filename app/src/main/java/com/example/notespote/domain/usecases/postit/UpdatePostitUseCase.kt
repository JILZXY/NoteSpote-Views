package com.example.notespote.domain.usecases.postits

import com.example.notespote.domain.model.Postit
import com.example.notespote.domain.repository.PostitRepository
import javax.inject.Inject

class UpdatePostitUseCase @Inject constructor(
    private val postitRepository: PostitRepository
) {
    suspend operator fun invoke(postit: Postit): Result<Unit> {
        return postitRepository.updatePostit(postit)
    }
}

