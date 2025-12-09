package com.example.notespote.domain.usecases.postits

import com.example.notespote.domain.model.Postit
import com.example.notespote.domain.repository.PostitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostitsByApunteUseCase @Inject constructor(
    private val postitRepository: PostitRepository
) {
    operator fun invoke(apunteId: String): Flow<Result<List<Postit>>> {
        return postitRepository.getPostitsByApunte(apunteId)
    }
}