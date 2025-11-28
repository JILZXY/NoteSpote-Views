package com.example.notespote.domain.usecases.notes

import com.example.notespote.domain.model.Apunte
import com.example.notespote.domain.repository.ApunteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetApuntesByUserUseCase @Inject constructor(
    private val apunteRepository: ApunteRepository
) {
    operator fun invoke(userId: String): Flow<Result<List<Apunte>>> {
        return apunteRepository.getApuntesByUser(userId)
    }
}