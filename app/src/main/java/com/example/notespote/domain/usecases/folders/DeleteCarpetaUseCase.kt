package com.example.notespote.domain.usecases.folders

import com.example.notespote.domain.repository.CarpetaRepository
import javax.inject.Inject

class DeleteCarpetaUseCase @Inject constructor(
    private val carpetaRepository: CarpetaRepository
) {
    suspend operator fun invoke(carpetaId: String): Result<Unit> {
        return carpetaRepository.deleteCarpeta(carpetaId)
    }
}