package com.example.notespote.domain.usecases.notes

import com.example.notespote.domain.model.Apunte
import com.example.notespote.domain.repository.ApunteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentApuntesUseCase @Inject constructor(
    private val repository: ApunteRepository
) {
    operator fun invoke(userId: String, limit: Int): Flow<Result<List<Apunte>>> {
        return repository.getRecentApuntes(userId, limit)
    }
}