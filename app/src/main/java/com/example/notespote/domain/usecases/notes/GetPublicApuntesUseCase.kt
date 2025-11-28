package com.example.notespote.domain.usecases.notes

import com.example.notespote.domain.model.Apunte
import com.example.notespote.domain.repository.ApunteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPublicApuntesUseCase @Inject constructor(
    private val apunteRepository: ApunteRepository
) {
    operator fun invoke(limit: Int = 50): Flow<Result<List<Apunte>>> {
        return apunteRepository.getPublicApuntes(limit)
    }
}