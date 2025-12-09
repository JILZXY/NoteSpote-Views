package com.example.notespote.domain.usecases.notes

import com.example.notespote.domain.repository.ApunteRepository
import javax.inject.Inject

class DeleteApunteUseCase @Inject constructor(
    private val apunteRepository: ApunteRepository
) {
    suspend operator fun invoke(apunteId: String): Result<Unit> {
        return apunteRepository.deleteApunte(apunteId)
    }
}