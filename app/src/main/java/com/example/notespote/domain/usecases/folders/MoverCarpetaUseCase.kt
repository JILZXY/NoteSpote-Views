package com.example.notespote.domain.usecases.folders

import com.example.notespote.domain.repository.CarpetaRepository
import javax.inject.Inject

class MoverCarpetaUseCase @Inject constructor(
    private val carpetaRepository: CarpetaRepository
) {
    suspend operator fun invoke(carpetaId: String, nuevoPadreId: String?): Result<Unit> {
        if (carpetaId == nuevoPadreId) {
            return Result.failure(Exception("Una carpeta no puede ser padre de sí misma"))
        }

        return carpetaRepository.moverCarpeta(carpetaId, nuevoPadreId)
    }
}