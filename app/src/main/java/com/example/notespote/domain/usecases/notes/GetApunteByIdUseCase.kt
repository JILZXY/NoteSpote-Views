package com.example.notespote.domain.usecases.notes

import com.example.notespote.domain.model.ApunteDetallado
import com.example.notespote.domain.repository.ApunteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetApunteByIdUseCase @Inject constructor(
    private val apunteRepository: ApunteRepository
) {
    operator fun invoke(apunteId: String): Flow<Result<ApunteDetallado>> {
        return apunteRepository.getApunteById(apunteId)
    }
}