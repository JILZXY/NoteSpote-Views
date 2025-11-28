package com.example.notespote.domain.usecases.notes

import com.example.notespote.domain.model.Apunte
import com.example.notespote.domain.repository.ApunteRepository
import javax.inject.Inject

class UpdateApunteUseCase @Inject constructor(
    private val apunteRepository: ApunteRepository
) {
    suspend operator fun invoke(apunte: Apunte): Result<Unit> {
        if (apunte.titulo.isBlank()) {
            return Result.failure(Exception("El título no puede estar vacío"))
        }

        return apunteRepository.updateApunte(apunte)
    }
}